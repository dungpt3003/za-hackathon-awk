package com.zalo.hackathon;

import com.zalo.hackathon.application.HackathonApplication;
import com.zalo.hackathon.detector.SentimentDetector;
import io.undertow.Undertow;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

public class WebhookMain {
    public static void main(String args[]) {
        SentimentDetector.getInstance();
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerCfg = config.getLoggerConfig("io.netty");
        loggerCfg.setLevel(Level.OFF); //Using this level disables the logger.
        ctx.updateLoggers();


        String host = args[0];
        int port = Integer.parseInt(args[1]);


        Undertow.Builder builder = Undertow.builder().addHttpListener(port, host);
        UndertowJaxrsServer server = new UndertowJaxrsServer().start(builder);
        server.deploy(HackathonApplication.class, "/");
    }
}
