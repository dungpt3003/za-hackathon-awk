package com.zalo.hackathon.controller;

import com.zalo.hackathon.utils.LogCenter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/")
public class HackathonController {
    private static Logger LOG = LogManager.getLogger(HackathonController.class);

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


        LogCenter.info(LOG, "Receive message" + message);

        return "Receive message " + message;
    }
}
