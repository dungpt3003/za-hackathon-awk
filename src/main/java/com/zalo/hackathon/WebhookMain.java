package com.zalo.hackathon;

import com.zalo.hackathon.application.HackathonApplication;
import io.undertow.Undertow;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

public class WebhookMain {
    public static void main(String args[]) {
        Undertow.Builder builder = Undertow.builder().addHttpListener(80, "0.0.0.0");
        UndertowJaxrsServer server = new UndertowJaxrsServer().start(builder);
        server.deploy(HackathonApplication.class, "/");
    }
}
