
package com.zalo.hackathon.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Order {

    @SerializedName("buy_now")
    private String mBuyNow;

    public String getBuyNow() {
        return mBuyNow;
    }

    public void setBuyNow(String buyNow) {
        mBuyNow = buyNow;
    }

}
