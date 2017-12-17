package com.zalo.hackathon.utils;

public class ZaStringUtils {
    public static String normalize(String content) {
        return org.apache.commons.lang3.StringUtils.stripAccents(content).toLowerCase().replaceAll("đ", "d");
    }

    public static String beautifulNumber(int number) {
        return String.format("%,8d%n", number);
    }
}
