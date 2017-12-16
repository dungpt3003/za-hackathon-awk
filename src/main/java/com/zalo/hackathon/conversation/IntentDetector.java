package com.zalo.hackathon.conversation;

import java.util.HashSet;
import java.util.Set;

public class IntentDetector {
    private static final String KEYWORDS_ASK_PRICE[] = new String[]{
            "giá",
            "price",
            "bao nhiêu",
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

    public static Set<Intent> detect(String message) {
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
            }

            if (check) {
                intents.add(intent);
            }
        }

        return intents;
    }

    public static boolean checkIntent(String[] keywords, String message) {
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    public static void main(String args[]) {
        System.out.println(detect("mình muốn tìm áo len"));
    }

}
