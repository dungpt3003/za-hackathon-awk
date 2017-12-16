
package com.zalo.hackathon.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.util.List;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class FullSaleInfo {

    @SerializedName("call")
    private String mCall;
    @SerializedName("listCmts")
    private List<ListCmt> mListCmts;
    @SerializedName("order")
    private List<Order> mOrder;
    @SerializedName("promo")
    private List<String> mPromo;

    public String getCall() {
        return mCall;
    }

    public void setCall(String call) {
        mCall = call;
    }


    public List<ListCmt> getListCmts() {
        return mListCmts;
    }

    public void setListCmts(List<ListCmt> listCmts) {
        mListCmts = listCmts;
    }

    public List<Order> getOrder() {
        return mOrder;
    }

    public void setOrder(List<Order> order) {
        mOrder = order;
    }

    public List<String> getPromo() {
        return mPromo;
    }

    public void setPromo(List<String> promo) {
        mPromo = promo;
    }

}
