package com.zalo.hackathon.controller;

public class UserMessage {
    String event;
    long oaid;
    long fromuid;
    long appid;
    String msgid;
    String message;
    String href;
    String thumb;
    long timestamp;
    String mac;
    String order;

    public UserMessage(String event, long oaid, long fromuid, long appid, String msgid, String message, String href, String thumb, long timestamp, String mac, String order) {
        this.event = event;
        this.oaid = oaid;
        this.fromuid = fromuid;
        this.appid = appid;
        this.msgid = msgid;
        this.message = message;
        this.href = href;
        this.thumb = thumb;
        this.timestamp = timestamp;
        this.mac = mac;
        this.order = order;
    }

    public String getOrder() {
        return order;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public long getOaid() {
        return oaid;
    }

    public void setOaid(long oaid) {
        this.oaid = oaid;
    }

    public long getFromuid() {
        return fromuid;
    }

    public void setFromuid(long fromuid) {
        this.fromuid = fromuid;
    }

    public long getAppid() {
        return appid;
    }

    public void setAppid(long appid) {
        this.appid = appid;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "UserMessage{" +
                "event='" + event + '\'' +
                ", oaid=" + oaid +
                ", fromuid=" + fromuid +
                ", appid=" + appid +
                ", msgid='" + msgid + '\'' +
                ", message='" + message + '\'' +
                ", href='" + href + '\'' +
                ", thumb='" + thumb + '\'' +
                ", timestamp=" + timestamp +
                ", mac='" + mac + '\'' +
                ", order='" + order + '\'' +
                '}';
    }
}
