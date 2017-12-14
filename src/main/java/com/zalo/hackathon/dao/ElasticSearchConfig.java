
/*
 * Copyright (C) VNG Corp - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Chien Nguyen Dang<dangchienhsgs@gmail.com>, September 2016
 */

package com.zalo.hackathon.dao;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dangchienhsgs on 30/09/2016.
 */
public class ElasticSearchConfig {
    private String host;
    private int port;
    @SerializedName("cluster_name")
    private String clusterName;

    public ElasticSearchConfig(String host, int port, String clusterName) {
        this.host = host;
        this.port = port;
        this.clusterName = clusterName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
