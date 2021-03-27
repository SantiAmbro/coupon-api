package challenge.service;

import challenge.exception.InsufficientAmountException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class CouponApiService {
    private static final String ML_ITEMS_API_URL = "https://api.mercadolibre.com/items";
    private static final String ID_KEY = "id";
    private static final String IDS_KEY = "ids";
    private static final String BODY_KEY = "body";
    private static final String PRICE_KEY = "price";
    private static final String ITEM_IDS_KEY = "item_ids";
    private static final String AMOUNT_KEY = "amount";
    private static final String TOTAL_KEY = "total";
    private Float totalApplied;

    /**
     * Method that consults Mercado Libre's API to find out the price of an item from a list of items ids.
     * @param ids String value of list of items to search.
     * @return The information of the items. (item id and price).
     * @throws JsonProcessingException after processing response.
     */
    public Map<String, Float> getMLItemsPrice(String ids) throws JsonProcessingException {
        //Set the URL to consult.
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ML_ITEMS_API_URL)
                .queryParam(IDS_KEY, ids);
        //Consult ML GET Endpoint (https://api.mercadolibre.com/items?ids=:ids).
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        //Read response from the endpoint.
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        //Create map with item_id and price from each item in the response.
        Map<String, Float> items = new HashMap<>();
        root.forEach(item -> {
            JsonNode itemBody = item.path(BODY_KEY);
            String itemId = itemBody.path(ID_KEY).asText();
            String priceString = itemBody.path(PRICE_KEY).asText();
            Float price = Float.valueOf(priceString);
            items.put(itemId, price);
        });
        return items;
    }

    /**
     * Method that calculates which were the item_ids to which the coupon was applied.
     * @param items Map of item_id with its price.
     * @param amount Coupon total amount to be applied.
     * @return List of item_ids.
     */
    public List<String> calculate(Map<String, Float> items, Float amount) {
        //Limit amount to two decimal places
        BigDecimal amountLimited = new BigDecimal(String.valueOf(amount)).setScale(2, BigDecimal.ROUND_DOWN);
        //Sort items by price.
        Map<String , Float> itemsSorted = sortItemsByPrice(items);
        //Add item_ids to which the coupon was applied and calculate the total applied.
        AtomicReference<Float> total = new AtomicReference<>(0f);
        List<String> freeItems = new ArrayList<>();
        itemsSorted.forEach((key, value) -> {
            Float aFloat = total.updateAndGet(v -> v + value);
            if (aFloat <= amountLimited.floatValue()) {
                freeItems.add(key);
                totalApplied = aFloat;
            }
        });
        //Throw status 404-NOT_FOUND after not being able to apply the coupon to at least one item.
        if(freeItems.isEmpty()){
            throw new InsufficientAmountException();
        }
        //Order items in a natural order
        freeItems.sort(Comparator.naturalOrder());
        return freeItems;
    }

    /**
     * Method that is responsible for applying the coupon
     * @param coupon Coupon to be applied
     * @return The list of items where the coupon was applied and the total applied
     * @throws JsonProcessingException after processing response
     */
    public Map<String, Object> applyCoupon(Map<String, Object> coupon) throws JsonProcessingException {
        //Remove duplicates from item_ids. Only one unit can be purchased per item_id.
        List<?> itemsIdsList = removeDuplicatesFromList((List<?>) coupon.get(ITEM_IDS_KEY));
        //Cast amount to Float object.
        Float amount = Float.valueOf(String.valueOf(coupon.get(AMOUNT_KEY)));
        //Remove "[" and "]" from item_ids value for consulting Mercado Libre's API
        String itemsIds = itemsIdsList.stream().map(String::valueOf).collect(Collectors.joining(","));
        //Consult ML's API and return items with price
        Map<String, Float> items = getMLItemsPrice(itemsIds);
        //Get the items_id to which the coupon was applied.
        List<String> freeItems = calculate(items, amount);
        //Create and populate the response with item_id and total of coupon applied.
        Map<String, Object> couponItems = new HashMap<>();
        couponItems.put(ITEM_IDS_KEY, freeItems);
        couponItems.put(TOTAL_KEY, totalApplied);
        return couponItems;
    }

    /**
     * Method that removes duplicates from a list.
     * @param list List to remove duplicates.
     * @return List without duplicates.
     */
    private List<?> removeDuplicatesFromList(List<?> list) {
        return list.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Method that receives a Map of items and orders them by price in ascending order.
     * @param items Items(item_id, price) to be sorted.
     * @return Items sorted by price.
     */
    private Map<String, Float> sortItemsByPrice(Map<String, Float> items) {
        return items.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
}
