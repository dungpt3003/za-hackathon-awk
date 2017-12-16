
package com.zalo.hackathon.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Policy {

    @SerializedName("timeship")
    private String mTimeship;

    public String getTimeship() {
        return mTimeship;
    }

    public void setTimeship(String timeship) {
        mTimeship = timeship;
    }
}
