package com.zalo.hackathon.conversation;

import com.zalo.hackathon.utils.ZaFileReader;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityDetector {

    private static Set<Entity> brands;
    private static Set<Entity> items;

    private static EntityDetector instance;

    private EntityDetector() {
        // load brands
        brands = new HashSet<>();

        new ZaFileReader(new File("data/brands.txt")) {
            @Override
            public void processLine(int indexLine, String line) {
                String brand = line.trim();
                brands.add(new Entity(brand, EntityType.BRAND));
            }
        };

        // load items
        items = new HashSet<>();
        new ZaFileReader(new File("data/categories_vn.txt")) {
            @Override
            public void processLine(int indexLine, String line) {
                int indexSpace = line.indexOf(" ");
//                int id = Integer.parseInt(line.substring(0, indexSpace));

                List<String> names = Arrays.stream(line.substring(indexSpace, line.length()).split(",")).map(x -> x.trim()).collect(Collectors.toList());
                for (String item : names) {
                    items.add(new Entity(item, EntityType.CLOTHING_TYPE));
                }
            }
        };
    }

    public static EntityDetector getInstance() {
        if (instance == null) {
            instance = new EntityDetector();
        }

        return instance;
    }

    public static void main(String args[]) {
        System.out.println(getInstance().detect("Tìm cho mình một chiếc áo Mango lông vũ và 1 chiếc áo Polo"));
    }

    public Set<Entity> detect(String message) {
        Set<Entity> entities = new HashSet<>();

        String nMessage = message.toLowerCase();
        for (Entity brand : brands) {
            if (nMessage.contains(brand.getValue())) {
                entities.add(brand);
            }
        }

        for (Entity item : items) {
            if (nMessage.contains(item.getValue())) {
                entities.add(item);
            }
        }

        return entities;
    }
}
