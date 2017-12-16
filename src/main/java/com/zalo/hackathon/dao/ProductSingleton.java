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


    private ProductSingleton() {
        productMap = new HashMap<>();
        try {
            String content = FileUtils.readFileToString(new File("data/result_rel_path.json"));
            JSONArray array = new JSONArray(content);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String productId = object.getString("productId");
                productMap.put(productId, object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
