
package com.zalo.hackathon.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.util.List;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class OAProduct {

    @SerializedName("accessory")
    private Object mAccessory;
    @SerializedName("cateIds")
    private List<Object> mCateIds;
    @SerializedName("code")
    private String mCode;
    @SerializedName("desc")
    private String mDesc;
    @SerializedName("display")
    private String mDisplay;
    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("oaId")
    private Long mOaId;
    @SerializedName("payment")
    private Long mPayment;
    @SerializedName("photoLinks")
    private List<String> mPhotoLinks;
    @SerializedName("photos")
    private List<String> mPhotos;
    @SerializedName("price")
    private Long mPrice;
    @SerializedName("productAskToBuyLink")
    private String mProductAskToBuyLink;
    @SerializedName("productDetailLink")
    private String mProductDetailLink;
    @SerializedName("productType")
    private String mProductType;
    @SerializedName("updateTime")
    private Long mUpdateTime;

    public Object getAccessory() {
        return mAccessory;
    }

    public void setAccessory(Object accessory) {
        mAccessory = accessory;
    }

    public List<Object> getCateIds() {
        return mCateIds;
    }

    public void setCateIds(List<Object> cateIds) {
        mCateIds = cateIds;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }

    public String getDisplay() {
        return mDisplay;
    }

    public void setDisplay(String display) {
        mDisplay = display;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Long getOaId() {
        return mOaId;
    }

    public void setOaId(Long oaId) {
        mOaId = oaId;
    }

    public Long getPayment() {
        return mPayment;
    }

    public void setPayment(Long payment) {
        mPayment = payment;
    }

    public List<String> getPhotoLinks() {
        return mPhotoLinks;
    }

    public void setPhotoLinks(List<String> photoLinks) {
        mPhotoLinks = photoLinks;
    }

    public List<String> getPhotos() {
        return mPhotos;
    }

    public void setPhotos(List<String> photos) {
        mPhotos = photos;
    }

    public Long getPrice() {
        return mPrice;
    }

    public void setPrice(Long price) {
        mPrice = price;
    }

    public String getProductAskToBuyLink() {
        return mProductAskToBuyLink;
    }

    public void setProductAskToBuyLink(String productAskToBuyLink) {
        mProductAskToBuyLink = productAskToBuyLink;
    }

    public String getProductDetailLink() {
        return mProductDetailLink;
    }

    public void setProductDetailLink(String productDetailLink) {
        mProductDetailLink = productDetailLink;
    }

    public String getProductType() {
        return mProductType;
    }

    public void setProductType(String productType) {
        mProductType = productType;
    }

    public Long getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(Long updateTime) {
        mUpdateTime = updateTime;
    }

}
