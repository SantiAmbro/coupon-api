package challenge.controller;

import challenge.service.CouponApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("coupon")
@CrossOrigin
@RestController
public class CouponApiController {

    private CouponApiService couponApiService;
    @Autowired
    public CouponApiController(CouponApiService couponApiService) {
        this.couponApiService = couponApiService;
    }

    @GetMapping("/items")
    public Map<String, Float> getMLItemPrice(@RequestParam String ids) throws JsonProcessingException {
       return couponApiService.getMLItemsPrice(ids);
    }

    @PostMapping
    @ResponseBody
    public Map<String, Object> applyCoupon(@RequestBody Map<String, Object> body) throws JsonProcessingException {
        return couponApiService.applyCoupon(body);
    }
}



