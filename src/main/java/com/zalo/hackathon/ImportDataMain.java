package com.zalo.hackathon;

import com.google.gson.Gson;
import com.zalo.hackathon.dao.BaseElasticDao;
import com.zalo.hackathon.dao.ElasticSearchConfig;
import com.zalo.hackathon.model.Product;
import com.zalo.hackathon.utils.ZaStringUtils;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.*;

class ImportDataMain {
    public static void main(String args[]) throws Exception {
        List<Product> productList = new ArrayList<>();
        Gson gson = new Gson();

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
                "imgUrl"
        );

        String json = FileUtils.readFileToString(new File("data/zdata/final-rel-path.json"));
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
            if (category.contains("phu kien") || categories.contains("op_lung_dien_thoai")) {
                category = "phu kien";
            }

            params.put("id", params.get("productId").toString());
            params.put("detail", removeSpecialChar(detail));
            params.put("category", category);
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
}

