package com.zalo.hackathon.utils;

import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by chiennd on 07/06/2017.
 */
public class LogCenter {
    public static void info(Logger logger, String message) {
        logger.info(message);
    }

    public static void error(Logger logger, String message) {
        logger.error(message);
    }

    public static void debug(Logger logger, String message) {
        logger.debug(message);
    }

    public static void warning(Logger logger, String message) {
        logger.warn(message);
    }

    public static void exception(Logger logger, Exception e) {
        logger.error(getStackTrace(e));
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
