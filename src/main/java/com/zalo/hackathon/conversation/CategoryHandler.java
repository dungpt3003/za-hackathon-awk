package com.zalo.hackathon.conversation;

import com.zalo.hackathon.utils.ZaFileReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CategoryHandler {

    private static Map<Integer, String> vnCategories;
    private static Map<String, Integer> vnCategoriesReverse;

    private static Map<Integer, String> enCategories;
    private static Map<String, Integer> enCategoriesReverse;


    private static CategoryHandler instance;

    private CategoryHandler() {

    }

    public static void init() {
        vnCategories = new HashMap<>();
        vnCategoriesReverse = new HashMap<>();

        enCategories = new HashMap<>();
        enCategoriesReverse = new HashMap<>();

        new ZaFileReader(new File("data/categories_vn.txt")) {
            @Override
            public void processLine(int indexLine, String line) {
                int spaceIndex = line.indexOf(" ");
                String idStr = line.replace("\uFEFF", "").trim().substring(0, spaceIndex);
                int id = Integer.parseInt(idStr.trim());
                String name = line.substring(spaceIndex + 1, line.length());

                vnCategories.put(id, name);
                vnCategoriesReverse.put(name, id);
            }
        };

        System.out.println(vnCategories.size());

        new ZaFileReader(new File("data/categories_en.txt")) {
            @Override
            public void processLine(int indexLine, String line) {
                int spaceIndex = line.indexOf(" ");
                int id = Integer.parseInt(line.substring(0, spaceIndex));
                String name = line.substring(spaceIndex + 1, line.length());

                enCategories.put(id, name);
                enCategoriesReverse.put(name, id);
            }
        };

        System.out.println(enCategories.size());
    }

    public static CategoryHandler getInstance() {
        if (instance == null) {
            instance = new CategoryHandler();
            init();
            return instance;
        }

        return instance;
    }

    public static void main(String args[]) {
        int id = CategoryHandler.getInstance().getEnCategoriesReverse().get("polo shirt, sport shirt");
        System.out.println(CategoryHandler.getInstance().getVnCategories().get(id));
    }

    public Map<Integer, String> getEnCategories() {
        return enCategories;
    }

    public Map<String, Integer> getEnCategoriesReverse() {
        return enCategoriesReverse;
    }

    public Map<Integer, String> getVnCategories() {
        return vnCategories;
    }

    public Map<String, Integer> getVnCategoriesReverse() {
        return vnCategoriesReverse;
    }

    public String getVietnames(String name) {
        return vnCategories.get(getEnCategoriesReverse().get(name));
    }
}
