package challenge;

import challenge.exception.InsufficientAmountException;
import challenge.service.CouponApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = CouponApiService.class)
public class CouponApiServiceTest {

    private final CouponApiService couponApiService;
    private Map<String, Float> items;

    @Autowired
    public CouponApiServiceTest(CouponApiService couponApiService) {
        this.couponApiService = couponApiService;
    }

    @BeforeEach
    void initItems() {
        this.items = new HashMap<>();
        this.items.put("MLA1", 100f);
        this.items.put("MLA2", 210f);
        this.items.put("MLA3", 260f);
        this.items.put("MLA4", 80f);
        this.items.put("MLA5", 90f);
    }

    @Test
    @DisplayName("Challenge Example calculate method")
    void callMLApiService() throws JsonProcessingException {
        Map<String, Float> mlItemsPrice = this.couponApiService.getMLItemsPrice("MLA836736184,MLA630698511");
        //Test size
        assertThat(mlItemsPrice.size(), is(2));
        //Test map key
        assertThat(mlItemsPrice, IsMapContaining.hasKey("MLA630698511"));
    }

    @Test
    @DisplayName("Challenge Example calculate method")
    void calculateChallengeExample() {
        List<String> items_ids = this.couponApiService.calculate(this.items, 500f);
        assertEquals("[MLA1, MLA2, MLA4, MLA5]", items_ids.toString());
    }

    @Test
    @DisplayName("Challenge example should throw InsufficientAmountException")
    void shouldThrowNotFound() {
        assertThrows(InsufficientAmountException.class, () -> {
            this.couponApiService.calculate(this.items, 50f);
        });
    }

    @Test
    @DisplayName("Apply coupon")
    void applyCoupon() throws JsonProcessingException {
        Map<String, Object> body = new HashMap<>();
        body.put("item_ids",
                Arrays.asList(
                        "MLA630698511",
                        "MLA897483200",
                        "MLA862561926",
                        "MLA855118196",
                        "MLA872652199",
                        "MLA762058450",
                        "MLA780058948",
                        "MLA836736184",
                        "MLA881071275"));
        body.put("amount", 2000f);
        Map<String, Object> couponMap = this.couponApiService.applyCoupon(body);
        //Test size
        assertThat(couponMap.size(), is(2));
        //Test map key
        assertThat(couponMap, IsMapContaining.hasKey("item_ids"));
        assertThat(couponMap, IsMapContaining.hasKey("total"));
        //Test map value
        assertThat(couponMap, IsMapContaining.hasValue(1710.76f));
    }
}
