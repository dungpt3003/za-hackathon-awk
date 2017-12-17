package com.zalo.hackathon;

import okhttp3.MediaType;

public class Config {
//    public static final long OA_ID = 1415650639660905238L;
//    public static final String SECRET_KEY = "n6ARcLqdGIJH97738QDX";

    public static final long OA_ID = 3640732980561232171L;
    public static final String SECRET_KEY = "GskG32yULO9rLiYTbIQT";

    public static final String INDEX = "ut_it_tech";
    public static final String TYPE = "tech_devices";

    public static final String ELASTIC_HOST = "localhost";
    public static final int ELASTIC_PORT = 9300;
    public static final String ELASTIC_CLUSTER_NAME = "hackathon";
    public static final long DUNG_PT_UID = 6248692413216850869L;
    public static final String URL_POST_IMAGE = "http://45.124.94.45:7000/predict";
    public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String STAR_1_URL = "http://45.124.94.45:10000/1_stars.png";
    public static String STAR_2_URL = "http://45.124.94.45:10000/2_stars.png";
    public static String STAR_3_URL = "http://45.124.94.45:10000/3_stars.png";
    public static String STAR_4_URL = "http://45.124.94.45:10000/4_stars.png";
    public static String STAR_5_URL = "http://45.124.94.45:10000/5_stars.png";
}
