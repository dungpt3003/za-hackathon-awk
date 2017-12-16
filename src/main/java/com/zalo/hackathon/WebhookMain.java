package com.zalo.hackathon;

import com.zalo.hackathon.application.HackathonApplication;
import io.undertow.Undertow;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

public class WebhookMain {
    public static void main(String args[]) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);


        Undertow.Builder builder = Undertow.builder().addHttpListener(port, host);
        UndertowJaxrsServer server = new UndertowJaxrsServer().start(builder);
        server.deploy(HackathonApplication.class, "/");
    }
}
