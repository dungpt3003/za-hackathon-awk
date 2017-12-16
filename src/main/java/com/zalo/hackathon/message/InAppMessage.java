package com.zalo.hackathon.message;

import com.vng.zalo.sdk.oa.message.OpenInAppAction;

public class InAppMessage extends OpenInAppAction {
    String href;

    String action = "oa.open.inapp";

    String data;

    @Override
    public void setUrl(String url) {
        super.setUrl(url);
        this.href = url;
        this.data = url;
    }
}
