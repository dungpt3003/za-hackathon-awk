package com.zalo.hackathon;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zalo.hackathon.dao.BaseElasticDao;
import com.zalo.hackathon.dao.ElasticSearchConfig;
import com.zalo.hackathon.model.Product;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

public class ImportDataMain {
    private static Gson GSON = new Gson();

    private static BaseElasticDao elasticDao;

    public static void main(String args[]) throws Exception {
        String input = "data/robins.json";

        String json = FileUtils.readFileToString(new File(input));
        List<Product> productList = GSON.fromJson(json, new TypeToken<List<Product>>() {
        }.getType());

        elasticDao = new BaseElasticDao(new ElasticSearchConfig("localhost", 9300, Config.ELASTIC_CLUSTER_NAME));

        List<Map<String, Object>> products = new ArrayList<>();

        int i = 0;

        Set<String> brands = new HashSet<>();
        Set<String> categories = new HashSet<>();

        for (Product product : productList) {
            Map<String, Object> params = new HashMap<>();

            String brand = product.getMeta().getBrand().toLowerCase();

            params.put("id", product.getMeta().getSku().toLowerCase());
            params.put("name", product.getMeta().getName().toLowerCase());
            params.put("desc", product.getDescription().toLowerCase());
            params.put("category", product.getCategoryPath().toString().toLowerCase());
            params.put("brand", brand);
            params.put("returnPolicy", product.getReturnPolicy());
            params.put("link", product.getLink());

            brands.add(brand);
            categories.addAll(product.getCategoryPath().stream().map(x -> x.toLowerCase()).collect(Collectors.toSet()));


            StringBuilder builder = new StringBuilder();
            for (Object value : params.values()) {
                builder.append(value).append("\t");
            }

            params.put("detail", builder.toString());

            String sPriceString = product.getMeta().getSpecialPrice().replace(".", "");
            String priceString = product.getMeta().getPrice().replace(".", "");

            long specialPrice = NumberUtils.toLong(sPriceString, -1);
            long price = NumberUtils.toLong(priceString, -1);

            params.put("special_price", specialPrice);
            params.put("price", price);
            products.add(params);
        }

        elasticDao.index(products, Config.INDEX, Config.TYPE);
        BufferedWriter bufferedReader = new BufferedWriter(new FileWriter("data/categories.txt"));
        for (String brand : brands) {
            bufferedReader.write(brand + "\n");
        }
        bufferedReader.close();


        bufferedReader = new BufferedWriter(new FileWriter("data/brands.txt"));
        for (String brand : brands) {
            bufferedReader.write(brand + "\n");
        }
        bufferedReader.close();

        System.out.println(i);
    }
}
