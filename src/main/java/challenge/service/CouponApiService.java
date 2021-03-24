package challenge.service;

import challenge.exception.InsufficientAmountException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

    public Map<String, Float> getMLItemsPrice(String ids) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ML_ITEMS_API_URL)
                .queryParam(IDS_KEY, ids);
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
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

    private List<String> calculate(Map<String, Float> items, Float amount) {
        Map<String , Float> itemsSorted = sortItemsByPrice(items);
        AtomicReference<Float> total = new AtomicReference<>(0f);
        List<String> freeItems = new ArrayList<>();
        itemsSorted.forEach((key, value) -> {
            Float aFloat = total.updateAndGet(v -> v + value);
            if (aFloat <= amount) {
                freeItems.add(key);
            }
        });
        if(freeItems.isEmpty()){
            throw new InsufficientAmountException();
        }
        return freeItems;
    }

    public Map<String, Object> applyCoupon(Map<String, Object> body) throws JsonProcessingException {
        Map<String, Object> couponItems = new HashMap<>();
        List<?> itemsIdsList = removeDuplicatesFromList((List<?>) body.get(ITEM_IDS_KEY));
        Float amount = Float.valueOf(String.valueOf(body.get(AMOUNT_KEY)));
        String itemsIds = itemsIdsList.stream().map(String::valueOf).collect(Collectors.joining(","));
        Map<String, Float> items = getMLItemsPrice(itemsIds);
        List<String> freeItems = calculate(items, amount);
        couponItems.put(ITEM_IDS_KEY, freeItems);
        couponItems.put(TOTAL_KEY, amount);
        return couponItems;
    }

    private List<?> removeDuplicatesFromList(List<?> list) {
        return list.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private Map<String, Float> sortItemsByPrice(Map<String, Float> items) {
        return items.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
}
