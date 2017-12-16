package com.zalo.hackathon.model;

public class ProductInfo {

    String id;
    String url;
    String imageUrl;
    String title;
    String desc;
    String price;

    public ProductInfo(String id, String url, String imageUrl, String title, String desc, String price) {
        this.id = id;
        this.url = url;
        this.imageUrl = imageUrl;
        this.title = title;
        this.desc = desc;
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getPrice() {
        return price;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ProductInfo{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
