/*
 * Copyright (C) VNG Corp - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Chien Nguyen Dang<dangchienhsgs@gmail.com>, September 2016
 */

package com.zalo.hackathon.dao;

import com.zalo.hackathon.utils.LogCenter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created sby Nguyen Dang Chien on 13/10/2016.
 */
public class BaseElasticDao {
    private static Logger LOG = LogManager.getLogger(BaseElasticDao.class);
    protected Client client;

    public BaseElasticDao(ElasticSearchConfig config) throws UnknownHostException {
        Settings settings = Settings.builder().put("cluster.name", config.getClusterName()).build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(config.getHost()), config.getPort()));
    }

    public static void main(String args[]) throws Exception {
        BaseElasticDao dao = new BaseElasticDao(new ElasticSearchConfig("localhost", 9300, "elastic_vng"));
        Map<String, Object> value = new HashMap<>();
        value.put("name", "bk");
        value.put("id", 1);

        dao.index(value, "group", "group", "1");
    }

    protected void index(Map<String, Object> source, String index, String type, String id) {
        client.prepareIndex(index, type)
                .setSource(source)
                .setId(id)
                .execute()
                .actionGet();
    }

    public boolean index(List<Map<String, Object>> maps, String index, String type) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        for (Map<String, ?> map : maps) {
            bulkRequest.add(client.prepareIndex(index, type)
                    .setSource(map)
                    .setId((String) map.get("id"))
            );
        }

        BulkResponse responses = bulkRequest.execute().actionGet();
        if (responses.hasFailures()) {
            LOG.info(responses.buildFailureMessage());
            return false;
        }

        return true;
    }

    public GetResponse get(String id, String index, String type) {
        return client.prepareGet(index, type, id).get();
    }

    protected void update(Map<String, Object> source, String index, String type, String id) {
        client.prepareUpdate(index, type, id)
                .setDoc(source)
                .execute()
                .actionGet();
    }

    protected void upsert(Map<String, Object> source, String index, String type, String id) {
        client.prepareUpdate(index, type, id)
                .setDoc(source)
                .setUpsert(source)
                .execute()
                .actionGet();
    }

    public List<Map<String, Object>> queryAll(QueryBuilder query, String index, String type, int batch) {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(query)
                .setTimeout(new TimeValue(60000))
                .setRequestCache(true)
                .setFrom(0)
                .setSize(batch)
                .setExplain(true);
        searchRequestBuilder.setScroll(new TimeValue(60000));
        searchRequestBuilder.setTypes(type);
        SearchResponse scrollResp = searchRequestBuilder.execute().actionGet();

        int from = 0;
        LogCenter.info(LOG, "Search " + index
                + " from " + from
                + " size " + batch);

        int scrollTime = 1;

        List<Map<String, Object>> result = new ArrayList<>();

        while (true) {
            LogCenter.info(LOG, "Took " + scrollResp.getTook()
                    + " hit " + scrollResp.getHits().getHits().length
                    + " scroll time " + scrollTime);

            for (SearchHit hit : scrollResp.getHits().getHits()) {
                if (hit.getSource() != null) {
                    result.add(hit.getSource());
                }
            }


            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000))
                    .execute().actionGet();
            if (scrollResp.getHits().getHits().length == 0) break;
            else scrollTime++;
        }

        return result;
    }

    public void queryAll(QueryBuilder query, String index, String type, int batch, ItemRunnable runnable) {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(query)
                .setTimeout(new TimeValue(60000))
                .setRequestCache(true)
                .setFrom(0)
                .setSize(batch)
                .setExplain(true);
        searchRequestBuilder.setScroll(new TimeValue(60000));
        searchRequestBuilder.setTypes(type);
        SearchResponse scrollResp = searchRequestBuilder.execute().actionGet();

        int from = 0;
        LogCenter.info(LOG, "Search " + index
                + " from " + from
                + " size " + batch);

        int scrollTime = 1;

        while (true) {
            LogCenter.info(LOG, "Took " + scrollResp.getTook()
                    + " hit " + scrollResp.getHits().getHits().length
                    + " scroll time " + scrollTime);

            for (SearchHit hit : scrollResp.getHits().getHits()) {
                if (hit.getSource() != null) {
                    runnable.process(hit.getSource());
                }
            }


            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000))
                    .execute().actionGet();
            if (scrollResp.getHits().getHits().length == 0) break;
            else scrollTime++;
        }
    }

    public SearchResponse query(QueryBuilder builder, String index, int size) {
        return client.prepareSearch(index)
                .setQuery(builder)
                .setSize(size)
                .execute()
                .actionGet();
    }

    public interface ItemRunnable {
        void process(Map<String, Object> value);
    }
}

