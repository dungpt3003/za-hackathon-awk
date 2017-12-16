package com.zalo.hackathon.controller;

import com.vng.zalo.sdk.APIException;
import com.vng.zalo.sdk.oa.ZaloOaClient;
import com.vng.zalo.sdk.oa.ZaloOaInfo;
import com.zalo.hackathon.Config;
import com.zalo.hackathon.conversation.TechConversation;
import com.zalo.hackathon.utils.LogCenter;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class HackathonController {
    private static Logger LOG = LogManager.getLogger(HackathonController.class);
    private static Map<Long, TechConversation> currentConversations;

    private ZaloOaClient oaClient;

    public HackathonController() {
        oaClient = new ZaloOaClient(new ZaloOaInfo(Config.OA_ID, Config.SECRET_KEY));

        if (currentConversations == null) {
            currentConversations = new HashMap<>();
        }
    }

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

        UserMessage msg = new UserMessage(
                event,
                Long.parseLong(oaid),
                Long.parseLong(fromuid),
                NumberUtils.toLong(appid, -1),
                msgid,
                message,
                href,
                thumb,
                Long.parseLong(timestamp),
                mac
        );

        LogCenter.info(LOG, "Receive message: " + msg);

        if (currentConversations.containsKey(msg.getFromuid())) {
            LogCenter.info(LOG, "Conversation of user " + fromuid + "existed");
            currentConversations.get(msg.getFromuid()).receiveMessage(msg);
        } else {
            try {
                LogCenter.info(LOG, "Conversation of user " + fromuid + " is not existed, create new conversation");
                TechConversation conversation = new TechConversation(msg.getFromuid(), oaClient);
                currentConversations.put(msg.getFromuid(), conversation);
                conversation.receiveMessage(msg);
            } catch (APIException e) {
                LogCenter.exception(LOG, e);
                LogCenter.info(LOG, "Can not create conversation of user " + fromuid);
            } catch (UnknownHostException e) {
                LogCenter.exception(LOG, e);
            }

        }

        return new JSONObject().put("error", "false").toString();
    }
}
