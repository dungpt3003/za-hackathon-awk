package com.zalo.hackathon;

import okhttp3.MediaType;

public class Config {
    public static final long OA_ID = 1415650639660905238L;
    public static final String SECRET_KEY = "n6ARcLqdGIJH97738QDX";

    public static final String INDEX = "ut_it_shop";
    public static final String TYPE = "clothing";

    public static final String ELASTIC_HOST = "localhost";
    public static final int ELASTIC_PORT = 9300;
    public static final String ELASTIC_CLUSTER_NAME = "hackathon";
    public static final String URL_POST_IMAGE = "http://45.124.94.45:7000/predict";
    public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");


}
