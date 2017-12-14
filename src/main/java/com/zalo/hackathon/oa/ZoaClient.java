package com.zalo.hackathon.oa;

import com.vng.zalo.sdk.APIException;
import com.vng.zalo.sdk.oa.ZaloOaClient;
import com.vng.zalo.sdk.oa.ZaloOaInfo;
import com.zalo.hackathon.Config;

public class ZoaClient {
    private ZaloOaClient client;

    public ZoaClient(ZaloOaInfo info) {
        this.client = new ZaloOaClient(info);
    }

    public static void main(String args[]) throws Exception {
        ZaloOaClient client = new ZaloOaClient(new ZaloOaInfo(Config.OA_ID, Config.SECRET_KEY));
//        JsonObject jsonObject = client.sendTextMessage(2540080485971043358L, "Hello World");
        System.out.println(client.getMessageStatus("cbfc03fd2ab505eb5ca4"));

    }

    public void sendMessage(int userId) throws APIException {
        client.sendTextMessage(userId, "Hello Chien");
    }
}
