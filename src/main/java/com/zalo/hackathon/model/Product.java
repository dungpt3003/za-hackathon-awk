
package com.zalo.hackathon.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.util.Map;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Product {

    @SerializedName("bginfo")
    private Map<String, String> mBginfo;
    @SerializedName("category")
    private String mCategory;
    @SerializedName("compr")
    private String mCompr;
    @SerializedName("fullSaleInfo")
    private FullSaleInfo mFullSaleInfo;
    @SerializedName("fullTechInfo")
    private Map<String, Map<String, String>> mFullTechInfo;
    @SerializedName("imgUrl")
    private String mImgUrl;
    @SerializedName("name")
    private String mName;
    @SerializedName("price")
    private String mPrice;
    @SerializedName("productId")
    private String mProductId;


    public Map<String, String> getmBginfo() {
        return mBginfo;
    }

    public void setmBginfo(Map<String, String> mBginfo) {
        this.mBginfo = mBginfo;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getCompr() {
        return mCompr;
    }

    public void setCompr(String compr) {
        mCompr = compr;
    }

    public FullSaleInfo getFullSaleInfo() {
        return mFullSaleInfo;
    }

    public void setFullSaleInfo(FullSaleInfo fullSaleInfo) {
        mFullSaleInfo = fullSaleInfo;
    }

    public Map<String, Map<String, String>> getmFullTechInfo() {
        return mFullTechInfo;
    }

    public void setmFullTechInfo(Map<String, Map<String, String>> mFullTechInfo) {
        this.mFullTechInfo = mFullTechInfo;
    }

    public String getImgUrl() {
        return mImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        mImgUrl = imgUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public String getProductId() {
        return mProductId;
    }

    public void setProductId(String productId) {
        mProductId = productId;
    }

}
