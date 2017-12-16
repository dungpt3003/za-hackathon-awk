package com.zalo.hackathon;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zalo.hackathon.dao.BaseElasticDao;
import com.zalo.hackathon.dao.ElasticSearchConfig;
import com.zalo.hackathon.model.OAProduct;
import com.zalo.hackathon.utils.ZaStringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

class ImportDataMain {
    private static Map<String, OAProduct> productMap;

    public static void main(String args[]) throws Exception {
        readProduct();
        BaseElasticDao baseElasticDao = new BaseElasticDao(new ElasticSearchConfig(Config.ELASTIC_HOST, Config.ELASTIC_PORT, Config.ELASTIC_CLUSTER_NAME));

        List<Map<String, Object>> data = new ArrayList<>();

        Set<String> categories = new HashSet<>();

        List<String> keys = Arrays.asList(
                "name",
                "category",
                "price",
                "fullSaleInfo",
                "fullTechInfo",
                "productId",
                "bginfo",
                "desc",
                "imgUrl"
        );

        String json = FileUtils.readFileToString(new File("data/result_rel_path.json"));
        JSONArray array = new JSONArray(json);


        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            String detail = "";
            Map<String, Object> params = new HashMap<>();
            for (String key : keys) {

                if (object.has(key)) {
                    String value = object.get(key).toString();
                    params.put(key, value);

                    detail = detail + value;

                } else {
                    params.put(key, "");
                }
            }

            if (object.has("imgUrl")) {
                String imgUrl = "http://45.124.94.45:10000/" + object.getString("imgUrl");
                params.put("imgUrl", imgUrl);
            } else {
                continue;
            }

            if (object.has("bginfo")) {
                String info = object.get("bginfo").toString();
                params.put("bginfo", info);
            }

            String category = ZaStringUtils.normalize(params.get("category").toString()).replace("-", " ");
            if (category.contains("phu kien")) {
                category = "phu kien";
            }

            int price = NumberUtils.toInt(params.get("price").toString().replace(".", "").replace("₫", ""), -1);
            if (price == -1) {
                continue;
            }


            params.put("price", price);
            params.put("id", params.get("productId").toString());
            params.put("detail", removeSpecialChar(detail));
            params.put("category", category);


            String productId = params.get("productId").toString();
            if (!productMap.containsKey(productId)) {
                continue;
            }

            params.put("store_id", productMap.get(productId).getId());
            params.put("productAskToBuyLink", productMap.get(productId).getProductAskToBuyLink());
            params.put("productDetailLink", productMap.get(productId).getProductDetailLink());
            params.put("desc", productMap.get(productId).getDesc());
            params.put("price", productMap.get(productId).getPrice());
            data.add(params);

            categories.add(category);
            System.out.println(params);
        }

        baseElasticDao.index(data, Config.INDEX, Config.TYPE);

        for (String category : categories) {
            System.out.println(category);
        }
    }

    public static String removeSpecialChar(String input) {
        input = input.replace("\"", " ");
        input = input.replace("[", " ");
        input = input.replace("]", " ");
        input = input.replace("]", " ");
        input = input.replace(":", " ");
        input = input.replace(",", "");

        return input;

    }

    public static void readProduct() throws Exception {
        Gson gson = new Gson();
        String content = FileUtils.readFileToString(new File("data/new.json"));
        Type type = new TypeToken<List<OAProduct>>() {
        }.getType();

        List<OAProduct> products = gson.fromJson(content, type);

        productMap = new HashMap<>();
        for (OAProduct product : products) {
            productMap.put(product.getCode(), product);
        }
    }
}

