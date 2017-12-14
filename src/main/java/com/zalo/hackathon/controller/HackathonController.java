package com.zalo.hackathon.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zalo.hackathon.utils.LogCenter;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.Arrays;

@Path("/")
public class HackathonController {
    private static Logger LOG = LogManager.getLogger(HackathonController.class);
    private OkHttpClient client = new OkHttpClient();

    private static final String URL_POST_IMAGE = "http://45.124.94.45:8080/predict";
    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

//    public HackathonController() {
//        client = new OkHttpClient();
//    }

    @Path("/test")
    @GET
    public String test() {
        return "Success";
    }

    @GET
    @Path("/")
    public String hello(@QueryParam("event") String event,
                        @QueryParam("oaid") String oaid,
                        @QueryParam("fromuid") String fromuid,
                        @QueryParam("appid") String appid,
                        @QueryParam("msgid") String msgid,
                        @QueryParam("message") String message,
                        @QueryParam("href") String href,
                        @QueryParam("thumb") String thumb,
                        @QueryParam("timestamp") String timestamp,
                        @QueryParam("mac") String mac) {

        LogCenter.info(LOG, "Receive " + message);
        return message;
//        switch (event) {
//            case "sendimagemsg":
//                LogCenter.info(LOG, "Receive image message: " + href);
//                JSONObject result = processSendImage(href);
//
//                if (result.getBoolean("error")) {
//                    return "Sorry, Mình không thể nhận diện ảnh này";
//                } else {
//                    return result.getString("message");
//                }
//        }
//
//        return "Hello World";
    }

    private JSONObject processSendImage(String href) {
        JSONObject object = new JSONObject()
                .put("service", "clothing")
                .put("data", Arrays.asList(href))
                .put("parameters", new JSONObject()
                        .put("output",
                                new JSONObject().put("best", 5))
                );

        Request request = new Request.Builder()
                .url(URL_POST_IMAGE)
                .post(RequestBody.create(JSON, object.toString()))
                .build();

        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();

            return new JSONObject().put("error", false).put("message", body);
        } catch (Exception e) {
            return new JSONObject().put("error", true).put("message", e.getMessage());
        }
    }

    public static void main(String args[]) {
        HackathonController controller = new HackathonController();
        JSONObject result = controller.processSendImage("http://maysonghanh.com/wp-content/uploads/sites/820/2017/03/áo-phông-cổ-dệt-màu-xanh-bích.jpg");

        System.out.println(result);
    }

}
