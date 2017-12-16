package com.zalo.hackathon.conversation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vng.zalo.sdk.APIException;
import com.vng.zalo.sdk.oa.ZaloOaClient;
import com.vng.zalo.sdk.oa.ZaloOaInfo;
import com.vng.zalo.sdk.oa.message.MsgImage;
import com.vng.zalo.sdk.oa.message.OpenInAppAction;
import com.zalo.hackathon.Config;
import com.zalo.hackathon.controller.UserMessage;
import com.zalo.hackathon.dao.BaseElasticDao;
import com.zalo.hackathon.dao.ElasticSearchConfig;
import com.zalo.hackathon.model.image_result.Class;
import com.zalo.hackathon.model.image_result.ImageDetectResult;
import com.zalo.hackathon.utils.LogCenter;
import com.zalo.hackathon.utils.MapUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.*;

public class ShopConversation {
    private static Logger LOG = LogManager.getLogger(ShopConversation.class);

    private long userId;

    private ZaloOaClient oaClient;

    private User user;

    private OkHttpClient client;

    private State currentState;

    private BaseElasticDao elasticDao;

    public ShopConversation(long userId, ZaloOaClient oaClient) throws APIException, UnknownHostException {
        this.userId = userId;
        this.oaClient = oaClient;
        this.client = new OkHttpClient();

        user = getProfile(userId);
        elasticDao = new BaseElasticDao(new ElasticSearchConfig(Config.ELASTIC_HOST, Config.ELASTIC_PORT, Config.ELASTIC_CLUSTER_NAME));
    }

    public static void main(String args[]) throws APIException, Exception {
        ZaloOaClient client = new ZaloOaClient(new ZaloOaInfo(Config.OA_ID, Config.SECRET_KEY));

        long userid = 2540080485971043358L; // user id;

        ShopConversation conversation = new ShopConversation(userid, client);

        System.out.println(conversation.processSendImage("https://mosaic01.ztat.net/nvg/media/large/RC/72/4D/00/7Q/11/RC724D007-Q11@22.jpg"));
    }

    public void receiveMessage(UserMessage message) {
        switch (message.getEvent()) {
            case "sendimagemsg":
                processImage(message);
                break;

            case "sendmsg":
                try {
                    processMessage(message.getMessage());
                } catch (Exception e) {
                    LogCenter.exception(LOG, e);
                }

                break;
        }
    }

    private JsonObject processImage(UserMessage message) {
        LogCenter.info(LOG, "Process image message " + message);
        if (currentState == State.SIMILAR_WAIT_IMAGE) {
            LogCenter.info(LOG, "Start to find similar item to item " + message.getHref());
            JSONObject result = processSendImage(message.getHref());

            try {
                oaClient.sendTextMessage(message.getFromuid(), result.toString());
            } catch (APIException e) {
                return null;
            }
        }

        return null;
    }

    private JsonObject processMessage(String message) throws Exception {
        if (message.trim().equals("#similar")) {
            LogCenter.info(LOG, "Change state to wait an image...");
            currentState = State.SIMILAR_WAIT_IMAGE;
            return null;
        }


        Set<Intent> intents = IntentDetector.detect(message);

        if (intents.contains(Intent.FIND_ITEM)) {
            Set<Entity> entities = EntityDetector.getInstance().detect(message);
        }

        return null;
    }

    private User getProfile(long id) throws APIException {
        JsonObject result = oaClient.getProfile(id);

        LogCenter.info(LOG, "Get profile  " + id + " result  " + result);

        String name = result.get("data").getAsJsonObject().get("displayName").getAsString();
        long birthDay = result.get("data").getAsJsonObject().get("birthDate").getAsLong();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(birthDay));

        int byear = calendar.get(Calendar.YEAR);
        int bmonth = calendar.get(Calendar.MONTH);
        int bday = calendar.get(Calendar.DATE);

        int gender = result.get("data").getAsJsonObject().get("userGender").getAsInt();

        return new User(name, byear, bday, bmonth, id, gender);
    }

    public JsonObject sendActionMessage(FixActionMessage actionMessage) throws APIException {
        return oaClient.sendActionMessage(userId, Arrays.asList(actionMessage));
    }


    private JSONObject processSendImage(String href) {
        LogCenter.info(LOG, "Start process image link " + href);
        JSONObject object = new JSONObject()
                .put("service", "clothing")
                .put("data", Arrays.asList(href))
                .put("parameters", new JSONObject()
                        .put("output",
                                new JSONObject().put("best", 5))
                );

        LogCenter.info(LOG, "Body request:  " + object.toString());
        Request request = new Request.Builder()
                .url(Config.URL_POST_IMAGE)
                .post(RequestBody.create(Config.JSON, object.toString()))
                .build();

        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();

            LogCenter.info(LOG, "Receive body: " + body);

            ImageDetectResult result = new Gson().fromJson(body, ImageDetectResult.class);
            List<com.zalo.hackathon.model.image_result.Class> classes = result.getBody().getPredictions().get(0).getClasses();

            Map<String, Double> scores = new HashMap<>();
            for (Class item : classes) {
                String name = CategoryHandler.getInstance().getVietnames(item.getCat());
                scores.put(name, item.getProb());
            }

            // search
            String searchKeyword = getSeachKeyword(scores);
            LogCenter.info(LOG, "Keyword: " + searchKeyword);
            List<Map<String, Object>> values = new ArrayList<>();
            SearchResponse resp = elasticDao
                    .query(QueryBuilders.boolQuery().must(QueryBuilders.queryStringQuery(searchKeyword.toString())), Config.INDEX, 100);

            for (int i = 0; i < resp.getHits().getHits().length; i++) {
                values.add(resp.getHits().getAt(i).getSource());
                System.out.println(values.get(i).get("link"));
            }


            LogCenter.info(LOG, "Classes predicted: " + scores);
            oaClient.sendTextMessage(user.getId(), scores.toString());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().put("error", true).put("message", e.getMessage());
        }
    }

    private String getSeachKeyword(Map<String, Double> scores) {
        scores = MapUtils.sortByValue(scores, false);

        StringBuilder keyword = new StringBuilder();

        double currentScore = 0;
        for (String name : scores.keySet()) {
            currentScore = currentScore + scores.get(name);
            keyword.append(" ").append(name);
            LogCenter.info(LOG, "Choose keyword " + name + " " + scores.get(name));
            if (currentScore > 0.5) {
                break;
            }
        }

        String result = keyword.toString();
        result = result.replaceAll(",", "");
        return result;
    }

    public JsonObject sendImageMessage(String imageId, String message) throws APIException {
        MsgImage image = new MsgImage();
        image.setImageid(imageId);
        image.setMessage(message);
        return oaClient.sendImageMessage(userId, image);
    }

    private enum State {
        SIMILAR_WAIT_IMAGE;
    }

    public static class FixActionMessage extends OpenInAppAction {
        String href;

        String action = "oa.open.inapp";

        String data;

        @Override
        public void setUrl(String url) {
            super.setUrl(url);
            this.href = url;
            this.data = url;
        }
    }
}
