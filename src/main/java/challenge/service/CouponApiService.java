package challenge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CouponApiService {
    private static final String ML_ITEMS_API_URL = "https://api.mercadolibre.com/items";

    public Map<String, Float> getMLItemsPrice(String ids) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ML_ITEMS_API_URL)
                .queryParam("ids", ids);
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        Map<String, Float> items = new HashMap<>();
        root.forEach(item -> {
            JsonNode itemBody = item.path("body");
            String itemId = itemBody.path("id").asText();
            String priceString = itemBody.path("price").asText();
            Float price = Float.valueOf(priceString);
            items.put(itemId, price);
        });
        return items;
    }

    public List<String> calculate(Map<String, Float> items, Float amount){
        return new ArrayList<>();
    }
}
