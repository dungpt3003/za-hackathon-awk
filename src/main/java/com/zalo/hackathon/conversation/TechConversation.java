package com.zalo.hackathon.conversation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vng.zalo.sdk.APIException;
import com.vng.zalo.sdk.oa.ZaloOaClient;
import com.vng.zalo.sdk.oa.ZaloOaInfo;
import com.vng.zalo.sdk.oa.message.MsgAction;
import com.vng.zalo.sdk.oa.message.QueryHideAction;
import com.vng.zalo.sdk.oa.message.QueryShowAction;
import com.zalo.hackathon.Config;
import com.zalo.hackathon.controller.UserMessage;
import com.zalo.hackathon.dao.BaseElasticDao;
import com.zalo.hackathon.dao.ElasticSearchConfig;
import com.zalo.hackathon.detector.Entity;
import com.zalo.hackathon.detector.EntityDetector;
import com.zalo.hackathon.detector.EntityType;
import com.zalo.hackathon.message.InAppMessage;
import com.zalo.hackathon.model.ProductInfo;
import com.zalo.hackathon.utils.LogCenter;
import com.zalo.hackathon.utils.MapUtils;
import com.zalo.hackathon.utils.ZaStringUtils;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

public class TechConversation {
    public static final String CHUOT_MAY_TINH = "chuot may tinh";
    public static final String LOA_LAPTOP = "loa laptop";
    public static final String LAPTOP = "laptop";
    public static final String OPLUNG = "op lung dien thoai";
    public static final String USB = "usb";
    public static final String CAPDT = "cap dien thoai";
    public static final String DIEN_THOAI = "dien thoai";
    public static final String THE_NHO = "the nho dien thoai";
    public static final String PHU_KIEN = "phu kien";
    public static final String SAC_DIEN_THOAI = "sac dtdd";
    public static final String TAI_NGHE = "tai nghe";
    public static final String MAY_TINH_BANG = "may tinh bang";
    public static final String SHOW_MORE_COMMAND = "#Xem thêm sản phẩm";
    private static final String SHOW_MORE_IMAGE = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a3/More_Icon_C.svg/500px-More_Icon_C.svg.png";

    private static final String SORRY_MESSAGE = "Xin lỗi bạn, Minibot đang trong quá trình xây dựng nên vẫn còn lỗi, chưa thể trả lời được bạn câu hỏi này, Mong bạn thông cảm :(";

    public static int SHOW_MORE_BATCH = 3;
    private static Logger LOG = LogManager.getLogger(TechConversation.class);
    private static BaseElasticDao elasticDao;
    private long userId;
    private ZaloOaClient oaClient;

    private User user;
    private OkHttpClient client;
    private State currentState;


    private ProductInfo currentProduct;

    private List<ProductInfo> cachedProducts;
    private List<ProductInfo> currentShowProducts;
    private int currentIndexProduct;


    public TechConversation(long userId, ZaloOaClient oaClient) throws APIException, UnknownHostException {
        this.userId = userId;
        this.oaClient = oaClient;
        this.client = new OkHttpClient();

        user = getProfile(userId);
        elasticDao = new BaseElasticDao(new ElasticSearchConfig(Config.ELASTIC_HOST, Config.ELASTIC_PORT, Config.ELASTIC_CLUSTER_NAME));
    }

    private User getProfile(long id) throws APIException {
        JsonObject result = oaClient.getProfile(id);

        LogCenter.info(LOG, "Get profile  " + id + " result  " + result);

        String name = result.get("data").getAsJsonObject().get("displayName").getAsString();
        long birthDay = result.get("data").getAsJsonObject().get("birthDate").getAsLong();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(birthDay));

        int byear = calendar.get(Calendar.YEAR);
        int bmonth = calendar.get(Calendar.MONTH);
        int bday = calendar.get(Calendar.DATE);

        int gender = result.get("data").getAsJsonObject().get("userGender").getAsInt();

        return new User(name, byear, bday, bmonth, id, gender);
    }

    public String receiveMessage(UserMessage message) {
        try {
            switch (message.getEvent()) {
                case "sendmsg":
                    processRawMessage(message.getMessage());
                    break;
            }


        } catch (APIException e) {
            LogCenter.exception(LOG, e);
            try {
                oaClient.sendTextMessage(userId, "Xin lỗi bạn " + user.getDisplayName() + ", Minibot đang trong quá trình xây dựng nên vẫn còn lỗi, chưa thể trả lời được bạn câu hỏi này, Mong bạn thông cảm :(");
            } catch (Exception e2) {
                LogCenter.exception(LOG, e);
            }
        }
        return "OK";
    }

    public static void main(String args[]) throws Exception {
        ZaloOaClient client = new ZaloOaClient(new ZaloOaInfo(Config.OA_ID, Config.SECRET_KEY));

        long userid = 2540080485971043358L; // user id;

        TechConversation conversation = new TechConversation(userid, client);

        conversation.processRawMessage("Tôi muốn tìm điện thoại samsung");
        conversation.processRawMessage("Cho mình xem thông số sản phẩm số 2");
        conversation.processRawMessage("Con này dùng hệ điều hành gì nhỉ ?");

    }

    public Map<String, String> parseToMap(String json) {
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public void processShowMore() throws APIException {
        LogCenter.info(LOG, "Process show more command, current index = " + currentIndexProduct);
        List<ProductInfo> newShowProducts = cachedProducts.subList(currentIndexProduct, Math.min(currentIndexProduct + SHOW_MORE_BATCH, cachedProducts.size()));
        sendProduct(currentShowProducts);
        if (newShowProducts.size() == 0) {
            oaClient.sendTextMessage(userId, "Xin lỗi bạn, không còn sản phẩm nào trong kết quả tìm kiếm");
            return;
        } else {
            sendProduct(newShowProducts);
            currentShowProducts = newShowProducts;
            currentIndexProduct += SHOW_MORE_BATCH;
        }
    }

    public void processRawMessage(String message) throws APIException {
        LogCenter.info(LOG, "Process raw message: " + message);
        Map<EntityType, List<Entity>> entities = EntityDetector.getInstance().detect(message);
        Set<Intent> intents = IntentDetector.detect(message, entities);

        if (StringUtils.equals(message, SHOW_MORE_COMMAND)) {
            processShowMore();
            return;
        }

        if (intents.contains(Intent.ASK_DETAIL)) {
            askDetail(entities, intents);
            return;
        }

        if (intents.contains(Intent.FIND_ITEM)) {
            findItem(entities, intents);
            return;
        }

        if (intents.contains(Intent.ASK_FULL_TECH)) {
            askFullTech(entities, intents);
            return;
        }

        if (intents.contains(Intent.BUY)) {
            oaClient.sendTextMessage(userId, "Bạn " + user.getDisplayName() + " có thể ấn vào ảnh sản phẩm để mua nhé :) ");
            return;
        }

        oaClient.sendTextMessage(userId, SORRY_MESSAGE);
    }

    public void askFullTech(Map<EntityType, List<Entity>> entities, Set<Intent> intents) throws APIException {
        currentState = State.STATE_ASK_DETAIL;
        if (currentProduct != null) {
            Map<String, Object> params = elasticDao.get(currentProduct.getId(), Config.INDEX, Config.TYPE).getSourceAsMap();
            String fullTechInfo = params.get("fullTechInfo").toString();
            Map<String, Map<String, String>> mapValue = parseFullTechInfo(fullTechInfo);
            StringBuilder replyMessage = new StringBuilder().append("Thông số kĩ thuật của sản phẩm " + currentProduct.getTitle() + " đây ạ: ").append("\n");
            oaClient.sendTextMessage(userId, replyMessage.toString());
            for (String key : mapValue.keySet()) {
                replyMessage = new StringBuilder();
                replyMessage.append(key + ":").append("\n");
                for (String localKey : mapValue.get(key).keySet()) {
                    replyMessage.append(localKey).append(":").append(" ").append(mapValue.get(key).get(localKey)).append("\n");
                }

                replyMessage.append("-------------------").append("\n");

                boolean check = false;
                for (Entity entity : entities.get(EntityType.FULL_TECH_ITEM)) {
                    if (ZaStringUtils.normalize(replyMessage.toString()).contains(ZaStringUtils.normalize(entity.getValue()))) {
                        check = true;
                        break;
                    }
                }
                if (check) {
                    LogCenter.info(LOG, "Send to user " + userId + ": " + replyMessage.toString());
                    oaClient.sendTextMessage(userId, replyMessage.toString());
                }
            }

            return;
        }

        oaClient.sendTextMessage(userId, "Xin lỗi bạn, mình nghĩ bạn đang định hỏi gì đó nhưng mình chưa hiểu lắm, bạn có thể nói rõ hơn được không ?");
    }

    public Map<String, Map<String, String>> parseFullTechInfo(String json) {
        Type type = new TypeToken<Map<String, Map<String, String>>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public void askDetail(Map<EntityType, List<Entity>> entities, Set<Intent> intents) throws APIException {
        currentState = State.STATE_ASK_DETAIL;
        if (entities.getOrDefault(EntityType.ORDER, new ArrayList<>()).size() > 0) {
            Entity entity = entities.get(EntityType.ORDER).get(0);
            int order = Integer.parseInt(entity.getValue());

            oaClient.sendTextMessage(userId, "Đợi mình chút nha, mình đang tìm thông số của sản phẩm thứ " + order);
            ProductInfo productInfo = currentShowProducts.get(order - 1);

            String id = productInfo.getId();
            Map<String, Object> product = elasticDao.get(id, Config.INDEX, Config.TYPE).getSourceAsMap();

            if (StringUtils.isEmpty(product.getOrDefault("bginfo", "").toString().trim())) {
                LogCenter.info(LOG, "Product " + id + " do not have info");
                oaClient.sendTextMessage(userId, "Xin lỗi bạn " + user.getDisplayName() + " rất nhiều, sản phẩm " + product.get("name") + " trên hệ thống không có cấu hình chi tiết :(");
                return;
            }

            String bgInfo = product.get("bginfo").toString();
            Map<String, String> mapValue = parseToMap(bgInfo);
            StringBuilder replyMessage = new StringBuilder().append("Thông số chung của sản phẩm " + productInfo.getTitle() + " đây ạ: ").append("\n");
            for (String key : mapValue.keySet()) {
                replyMessage.append(key).append(":").append(" ").append(mapValue.get(key)).append("\n");
            }

            oaClient.sendTextMessage(userId, replyMessage.toString());
            currentProduct = productInfo;
            return;
        }

        oaClient.sendTextMessage(userId, "Xin lỗi bạn, mình nghĩ bạn đang định hỏi gì đó nhưng mình chưa hiểu lắm, bạn có thể nói rõ hơn được không ?");
    }

    public void findItem(Map<EntityType, List<Entity>> entities, Set<Intent> intent) throws APIException {
        oaClient.sendTextMessage(userId, "Hệ thống đang tìm sản phẩm, bạn " + user.getDisplayName() + " đợi tí nhé :) ");

        LogCenter.info(LOG, "Detect intent: " + intent);
        LogCenter.info(LOG, "Detect entities: " + entities);

        StringBuilder keyword = new StringBuilder();
        EntityType mainType = detectMainEntity(entities);
        for (EntityType type : entities.keySet()) {
            if (type == EntityType.DIEN_THOAI) {
                continue;
            }

            for (Entity entity : entities.get(type)) {
                keyword.append(entity.getValue()).append(" ");
            }
        }

        LogCenter.info(LOG, "Keyword: " + keyword);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (mainType == EntityType.DIEN_THOAI) {
            boolQuery = boolQuery.must(QueryBuilders.termQuery("category", DIEN_THOAI));
        } else if (mainType == EntityType.PHU_KIEN) {
            boolQuery = boolQuery.must(QueryBuilders.termQuery("category", PHU_KIEN));
        }

        boolQuery = boolQuery.must(QueryBuilders.queryStringQuery(keyword.toString())).must(QueryBuilders.queryStringQuery(keyword.toString()));
        LogCenter.info(LOG, boolQuery.toString());
        SearchResponse response = elasticDao.query(boolQuery, Config.INDEX, 100);
        Map<Map<String, Object>, Float> results = convertSearchResponse(response);
        results = MapUtils.sortByValue(results, false);

        cachedProducts = new ArrayList<>();
        for (Map<String, Object> result : results.keySet()) {
            System.out.println(result.get("productId") + " " + result.get("name") + " " + results.get(result) + "  " + result.get("imgUrl"));
            String id = result.get("productId").toString();
            String productUrl = result.get("imgUrl").toString();
            String imgUrl = result.get("imgUrl").toString();
            String title = result.get("name").toString();
            String desc = result.get("price").toString();
            String price = result.get("price").toString();
            cachedProducts.add(new ProductInfo(id, productUrl, imgUrl, title, desc, price));
        }

        LogCenter.info(LOG, "Cache " + cachedProducts.size() + " products");

        currentIndexProduct = SHOW_MORE_BATCH;
        currentShowProducts = cachedProducts.subList(0, SHOW_MORE_BATCH);
        sendProduct(currentShowProducts);
    }

    public BoolQueryBuilder exact(String name, String value) {
        BoolQueryBuilder bool = QueryBuilders.boolQuery();

        for (String word : value.split(" ")) {
            bool = bool.should(QueryBuilders.termQuery(name, word));
        }

        return bool.minimumShouldMatch(value.split(" ").length);
    }

    private InAppMessage getInAppMessage(ProductInfo productInfo) {
        InAppMessage productMessage = new InAppMessage();
        productMessage.setUrl(productInfo.getUrl());
        productMessage.setDescription(productInfo.getDesc());
        productMessage.setPopup(popUp());
        productMessage.setThumb(productInfo.getImageUrl());
        productMessage.setTitle(productInfo.getTitle() + " - " + productInfo.getPrice());

        return productMessage;
    }

    private QueryHideAction getShowMoreMessage() {
        QueryHideAction showMore = new QueryHideFix();
        showMore.setData(SHOW_MORE_COMMAND);
        showMore.setDescription("Ấn để xem thêm");
        showMore.setThumb(SHOW_MORE_IMAGE);
        showMore.setTitle("Show more");

        return showMore;
    }

    private void sendProduct(List<ProductInfo> productInfos) throws APIException {

        LogCenter.info(LOG, "Product sent: " + productInfos);

        List<MsgAction> messages = new ArrayList<>();
        for (int i = 0; i < productInfos.size(); i++) {
            messages.add(getInAppMessage(productInfos.get(i)));
        }

        messages.add(getShowMoreMessage());
        oaClient.sendActionMessage(userId, messages);
    }

    private JsonObject popUp() {
        JsonObject popup = new JsonObject();
        popup.addProperty("title", "Chuyển tiếp");
        popup.addProperty("desc", "Bạn có đồng ý chuyển đến trang chính của shop ?");
        popup.addProperty("ok", "OK");
        popup.addProperty("cancel", "Cancel");

        return popup;
    }

    public Map<Map<String, Object>, Float> convertSearchResponse(SearchResponse response) {
        Map<Map<String, Object>, Float> resps = new HashMap<>();
        if (response.getHits().getHits().length == 0) {
            return resps;
        }

        for (SearchHit hit : response.getHits().getHits()) {
            resps.put(hit.getSource(), hit.getScore());
        }

        return resps;
    }

    public EntityType detectMainEntity(Map<EntityType, List<Entity>> map) {
        List<EntityType> keys = map.keySet().stream().filter(x -> map.getOrDefault(x, new ArrayList<>()).size() > 0).collect(Collectors.toList());
        if (keys.size() == 1) {
            return keys.get(0);
        }

        boolean checkHasPhone = map.getOrDefault(EntityType.DIEN_THOAI, new ArrayList<>()).size() > 0;
        boolean checkHasPhuKien = map.getOrDefault(EntityType.PHU_KIEN, new ArrayList<>()).size() > 0;

        if (checkHasPhone && checkHasPhuKien) {
            return EntityType.PHU_KIEN;
        }

        if (checkHasPhone) {
            return EntityType.DIEN_THOAI;
        }

        if (checkHasPhuKien) {
            return EntityType.PHU_KIEN;
        }

        return null;
    }

    private class QueryShowFix extends QueryShowAction {
        String action = "oa.query.hide";
    }

    private class QueryHideFix extends QueryHideAction {
        String action = "oa.query.hide";
    }

    private enum State {
        START_FIND,
        SEE_MORE_ITEM,
        STATE_ASK_DETAIL,
    }

}