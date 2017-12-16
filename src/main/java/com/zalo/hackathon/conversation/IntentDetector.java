package com.zalo.hackathon.conversation;

import com.zalo.hackathon.detector.Entity;
import com.zalo.hackathon.detector.EntityDetector;
import com.zalo.hackathon.detector.EntityType;
import com.zalo.hackathon.utils.ZaStringUtils;

import java.util.*;

public class IntentDetector {
    private static final String KEYWORDS_ASK_PRICE[] = new String[]{
            "giá",
            "price",
            "giá rổ",
    };

    private static final String KEYWORD_ASK_SIMILAR_ITEMS[] = new String[]{
            "similar",
            "tương tự",
            "giống",
            "cùng loại"
    };

    private static final String KEYWORD_FIND_ITEMS[] = new String[]{
            "tìm sản phẩm",
            "tìm",
            "có sản phẩm nào",
            "có chiếc áo nào",
            "có chiếc dày nào"
    };

    private static final String KEYWORD_ASK_DETAIL[] = new String[]{
            "chi tiết",
            "cấu hình sản phẩm",
            "cấu hình",
            "thông số"
    };

    private static final String KEYWORD_TU_VAN[] = new String[]{
            "thích",
            "hay đấy",
            "được đấy",
            "muốn mua sản phẩm",
            "thích sản phẩm này",
    };

    private static final String KEYWORD_XIN_CHAO[] = new String[]{
            "hello",
            "xin chào",
            "hi",
            "hey",
    };

    private static final String KEY_WORD_REVIEW[] = new String[]{
            "review",
            "đánh giá",
            "người dùng đánh giá",
            "đánh giá từ người dùng"
    };

    public static Set<Intent> detect(String message, Map<EntityType, List<Entity>> entities) {
        Set<Intent> intents = new HashSet<>();
        for (Intent intent : Intent.values()) {
            boolean check = false;
            switch (intent) {
                case ASK_PRICE:
                    check = checkIntent(KEYWORDS_ASK_PRICE, message);
                    break;

                case ASK_SIMILAR_ITEM:
                    check = checkIntent(KEYWORD_ASK_SIMILAR_ITEMS, message);
                    break;

                case FIND_ITEM:
                    check = checkIntent(KEYWORD_FIND_ITEMS, message);
                    break;
                case ASK_DETAIL:
                    check = checkIntent(KEYWORD_ASK_DETAIL, message);
                    break;
                case BUY:
                    check = checkIntent(KEYWORD_TU_VAN, message);
                    break;

                case ASK_FULL_TECH:
                    if (entities.getOrDefault(EntityType.FULL_TECH_ITEM, new ArrayList<>()).size() > 0) {
                        check = true;
                    }
                    break;

                case XIN_CHAO:
                    check = checkIntent(KEYWORD_XIN_CHAO, message);
                    break;

                case ASK_RATING:
                    check = checkIntent(KEY_WORD_REVIEW, message);
                    break;
            }

            if (check) {
                intents.add(intent);
            }

            if (entities.containsKey(EntityType.PRICE)) {
                intents.add(Intent.ASK_PRICE);
            }
        }

        return intents;
    }

    public static boolean checkIntent(String[] keywords, String message) {
        String mMessage = ZaStringUtils.normalize(message);
        for (String keyword : keywords) {
            if (mMessage.contains(ZaStringUtils.normalize(keyword))) {
                return true;
            }
        }

        return false;
    }

    public static void main(String args[]) {
        String message = "ram của Iphone X là bao nhiêu ?";
        System.out.println(detect(message, EntityDetector.getInstance().detect(message)));
    }

}
