package challenge.controller;

import challenge.service.CouponApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("coupon")
@CrossOrigin
@Api(value = "CouponApiController - Mercado Libre challenge Coupon API")
@RestController
public class CouponApiController {

    private final CouponApiService couponApiService;
    @Autowired
    public CouponApiController(CouponApiService couponApiService) {
        this.couponApiService = couponApiService;
    }

    @PostMapping
    @ApiOperation(value = "Retrieves the information of a map of items and the total of the coupon applied")
    @ResponseBody
    public Map<String, Object> applyCoupon(@ApiParam(value = "Coupon to be applied") @RequestBody Map<String, Object> coupon) throws JsonProcessingException {
        return couponApiService.applyCoupon(coupon);
    }
}



