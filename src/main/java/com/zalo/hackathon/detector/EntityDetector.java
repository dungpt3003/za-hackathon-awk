package com.zalo.hackathon.detector;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zalo.hackathon.utils.LogCenter;
import com.zalo.hackathon.utils.ZaStringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

public class EntityDetector {
    private static Logger LOG = LogManager.getLogger(EntityDetector.class);

    private static EntityDetector instance;

    private static Set<String> BRANDS = new HashSet<>(Arrays.asList(
            "samsung",
            "sony",
            "apple",
            "lg",
            "xiaomi",
            "phillips",
            "sandisk",
            "iphone",
            "galaxy",
            "xperia",
            "vivo"
    ));

    private static Set<String> DIEN_THOAI;

    private static Set<String> TABLET;

    private static Set<String> PHU_KIEN;

    private static Set<String> OP_LUNG = new HashSet<>(Arrays.asList(
            "ốp lưng",
            "ốp"
    ));

    private static Set<String> SAC = new HashSet<>(Arrays.asList(
            "sạc dự phòng",
            "pin",
            "sạc"
    ));

    private static Set<String> OPERATION = new HashSet<>(Arrays.asList(
            "android",
            "android 7.0",
            "android 5.0",
            "android 6.0",
            "ios",
            "symbian",
            "windows phone"
    ));


    private static Set<String> FULL_TECH_INFO = new HashSet<>(Arrays.asList(
            "he dieu hanh",
            "camera",
            "bo nho trong",
            "ram",
            "bo nho ngoai",
            "do phan giai",
            "bluetooth",
            "nfc",
            "wifi",
            "3g",
            "4g",
            "lte",
            "nano sim",
            "micro usb",
            "flash",
            "tien ich",
            "dung luong pin",
            "man hinh",
            "ket noi",
            "thong so ky thuat"
    ));

    private EntityDetector() throws Exception {
        String json = FileUtils.readFileToString(new File("data/zdata/categories.json"));
        Type listType = new TypeToken<Map<String, List<String>>>() {
        }.getType();

        PHU_KIEN = new HashSet<>();
        Map<String, List<String>> content = new Gson().fromJson(json, listType);

        DIEN_THOAI = new HashSet<>(content.get("dien thoai"));
        TABLET = new HashSet<>(content.get("may tinh bang"));
        PHU_KIEN.addAll(new HashSet<>(content.get("phu kien")));
    }

    public static EntityDetector getInstance() {
        if (instance == null) {
            try {
                instance = new EntityDetector();
            } catch (Exception e) {
                LogCenter.exception(LOG, e);
            }
        }

        return instance;
    }

    public static void main(String args[]) {
        System.out.println(EntityDetector.getInstance().detect("Mình muốn tìm điện thoại iphone"));

    }

    public static int convertToNumber(String word) {
        if (NumberUtils.isNumber(word)) {
            return NumberUtils.toInt(word);
        }

        switch (ZaStringUtils.normalize(word)) {
            case "mot":
                return 1;
            case "hai":
                return 2;
            case "ba":
                return 3;
            case "bon":
                return 4;
            case "nam":
                return 5;
            case "sau":
                return 6;
            case "bay":
                return 7;
            case "tam":
                return 8;
            case "chin":
                return 9;
        }

        return -1;
    }

    public List<Entity> detectByKeywords(String message, Set<String> keywords, EntityType entityType) {
        String temp = ZaStringUtils.normalize(message);
        List<Entity> entities = new ArrayList<>();
        for (String keyword : keywords) {
            if (temp.contains(ZaStringUtils.normalize(keyword))) {
                entities.add(new Entity(keyword.toLowerCase(), entityType));
            }
        }

        return entities;
    }

    public List<Entity> detectBrands(String message) {
        String temp = ZaStringUtils.normalize(message);

        List<Entity> entities = new ArrayList<>();
        for (String brandName : BRANDS) {
            if (temp.contains(brandName)) {
                entities.add(new Entity(brandName, EntityType.BRANDS));
            }
        }

        return entities;
    }

    public Map<EntityType, List<Entity>> detect(String message) {
        Map<EntityType, List<Entity>> result = new HashMap<>();

        for (EntityType type : EntityType.values()) {
            switch (type) {
                case BRANDS:
                    List<Entity> brands = detectBrands(message);
                    if (brands.size() > 0) {
                        result.put(type, brands);
                    }
                    break;
                case DIEN_THOAI:
                    List<Entity> mobile = detectByKeywords(message, DIEN_THOAI, EntityType.DIEN_THOAI);
                    if (mobile.size() > 0) {
                        result.put(type, mobile);
                    }
                    break;
                case TABLET:
                    List<Entity> tablet = detectByKeywords(message, TABLET, EntityType.TABLET);
                    if (tablet.size() > 0) {
                        result.put(type, tablet);
                    }

                    break;
                case ORDER:
                    List<Entity> order = detectOrder(message);
                    if (order.size() > 0) {
                        result.put(type, detectOrder(message));
                    }

                    break;
                case PHU_KIEN:
                    List<Entity> phuKien = detectByKeywords(message, PHU_KIEN, EntityType.PHU_KIEN);
                    if (phuKien.size() > 0) {
                        result.put(type, phuKien);
                    }
                    break;
                case FULL_TECH_ITEM:
                    List<Entity> techInfos = detectByKeywords(message, FULL_TECH_INFO, EntityType.FULL_TECH_ITEM);
                    if (techInfos.size() > 0) {
                        result.put(type, techInfos);
                    }

                    break;
                case OP_LUNG:
                    List<Entity> opLung = detectByKeywords(message, OP_LUNG, EntityType.OP_LUNG);
                    if (opLung.size() > 0) {
                        result.put(type, opLung);
                    }
                    break;

                case SAC_PIN:
                    List<Entity> sac = detectByKeywords(message, SAC, EntityType.SAC_PIN);
                    if (sac.size() > 0) {
                        result.put(type, sac);
                    }
                    break;

                case PRICE:
                    List<Entity> price = detectPrice(message);
                    if (price.size() > 0) {
                        result.put(type, price);
                    }
                    break;
            }
        }

        return result;
    }

    public List<Entity> detectPrice(String message) {
        String nMessage = ZaStringUtils.normalize(message);

        String words[] = nMessage.split(" ");

        List<Entity> entities = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals("trieu")) {
                int detectValue = convertToNumber(words[i - 1]);
                if (detectValue == -1) {
                    continue;
                }

                entities.add(new Entity(String.valueOf(detectValue * 1000000), EntityType.PRICE));
            }

            if (words[i].startsWith("tram")) {
                int detectValue = convertToNumber(words[i - 1]);
                if (detectValue == -1) {
                    continue;
                }

                entities.add(new Entity(String.valueOf(detectValue * 100000), EntityType.PRICE));
            }
        }

        return entities;
    }

    public List<Entity> detectOrder(String message) {
        String normalize = ZaStringUtils.normalize(message);

        List<Entity> entities = new ArrayList<>();
        if (normalize.contains("dau tien") || normalize.contains("thu nhat") || normalize.contains("thu 1")) {
            entities.add(new Entity("1", EntityType.ORDER));
        } else if (normalize.contains("thu hai") || normalize.contains("thu 2") || normalize.contains("giua")) {
            entities.add(new Entity("2", EntityType.ORDER));
        } else if (normalize.contains("thu ba") || normalize.contains("thu 3") || normalize.contains("cuoi cung")) {
            entities.add(new Entity("3", EntityType.ORDER));
        }

        return entities;
    }
}
