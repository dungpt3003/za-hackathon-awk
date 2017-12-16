package com.zalo.hackathon.dao;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ProductSingleton {
    private static Map<String, JSONObject> productMap;
    private static ProductSingleton instance;

    private static Map<String, String> productToCode = new HashMap<>();
    private static Map<String, String> codeToProduct = new HashMap<>();

    private static Map<String, String> codeToDetailLink = new HashMap<>();
    private static Map<String, String> codeToBuyLink = new HashMap<>();

    private ProductSingleton() {
        productMap = new HashMap<>();
        try {
            String content = FileUtils.readFileToString(new File("data/result_rel_path.json"));
            JSONArray array = new JSONArray(content);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String productId = object.getString("productId");
                String imgUrl = "http://45.124.94.45:10000/" + object.getString("imgUrl");
                object.put("imgUrl", imgUrl);
                productMap.put(productId, object);
            }


            content = FileUtils.readFileToString(new File("data/new.json"));
            JSONArray jsonArray = new JSONArray(content);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject product = jsonArray.getJSONObject(i);
                String productId = product.getString("id");
                String code = product.getString("code");
                String detailLink = product.getString("productDetailLink");
                String askToBuyLink = product.getString("productAskToBuyLink");

                codeToDetailLink.put(code, detailLink);
                codeToBuyLink.put(code, askToBuyLink);
                productToCode.put(productId, code);
                codeToProduct.put(code, productId);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getCodeToDetailLink() {
        return codeToDetailLink;
    }

    public Map<String, String> getCodeToBuyLink() {
        return codeToBuyLink;
    }

    public Map<String, String> getProductToCode() {
        return productToCode;
    }

    public Map<String, String> getCodeToProduct() {
        return codeToProduct;
    }

    public static ProductSingleton getInstance() {
        if (instance == null) {
            instance = new ProductSingleton();
        }

        return instance;
    }

    public static void main(String args[]) {
        System.out.println(ProductSingleton.getInstance().getProduct("139401"));
    }

    public JSONObject getProduct(String productId) {
        return productMap.get(productId);
    }
}
