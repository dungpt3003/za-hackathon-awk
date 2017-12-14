package com.zalo.hackathon.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class HackathonController {

    @GET
    @Path("/")
    public String hello() {
        return "Hello World";
    }
}
