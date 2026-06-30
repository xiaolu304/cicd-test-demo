package com.example.store.controller;

import com.example.store.shipping.IShippingStrategy;
import com.example.store.shipping.ShippingStrategyFactory;
import com.example.store.shipping.impl.OverseasWarehouseStrategy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "海外仓配送运费计算")
@RestController
@RequestMapping("/shipping/overseas")
public class OverseasShippingController {

    private final ShippingStrategyFactory shippingStrategyFactory;

    public OverseasShippingController(ShippingStrategyFactory shippingStrategyFactory) {
        this.shippingStrategyFactory = shippingStrategyFactory;
    }

    @Operation(summary = "计算海外仓配送运费")
    @GetMapping("/calculate")
    public ResponseEntity<?> calculate(
            @RequestParam double weight,
            @RequestParam double orderAmount,
            @RequestParam(defaultValue = "0") int userVipLevel,
            @RequestParam(defaultValue = "false") boolean isNewUser) {

        IShippingStrategy strategy = shippingStrategyFactory.getOverseasWarehouseStrategy();
        // 接口只支持 3 个参数：regionCode=0, weight, orderAmount
        double baseCost = strategy.calculateShipping(0, weight, orderAmount);

        // 强转后调用内部优惠方法，动态使用前端传入的VIP、新用户参数
        OverseasWarehouseStrategy overseas = (OverseasWarehouseStrategy) strategy;
        double afterVip = overseas.applyVipDiscount(baseCost, userVipLevel);
        double afterOrder = overseas.applyOrderAmountDiscount(afterVip, orderAmount);
        double finalCost = overseas.applyNewUserDiscount(afterOrder, isNewUser);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("deliveryType", "海外仓配送");
        result.put("weight", weight);
        result.put("orderAmount", orderAmount);
        result.put("vipLevel", userVipLevel);
        result.put("isNewUser", isNewUser);
        result.put("baseCost", baseCost);
        result.put("finalShippingCost", Math.max(finalCost, 0));

        return ResponseEntity.ok(result);
    }
}