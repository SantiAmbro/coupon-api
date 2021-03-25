package challenge.controller;

import challenge.service.CouponApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("coupon")
@CrossOrigin
@RestController
public class CouponApiController {

    private final CouponApiService couponApiService;
    @Autowired
    public CouponApiController(CouponApiService couponApiService) {
        this.couponApiService = couponApiService;
    }

    @PostMapping
    @ResponseBody
    public Map<String, Object> applyCoupon(@RequestBody Map<String, Object> body) throws JsonProcessingException {
        return couponApiService.applyCoupon(body);
    }
}



