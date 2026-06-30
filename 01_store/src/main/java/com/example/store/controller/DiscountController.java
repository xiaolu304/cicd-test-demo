package com.example.store.controller;

import com.example.store.strategy.DiscountStrategyFactory;
import com.example.store.strategy.IDiscountStrategy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "大促折扣计算")
@RestController
@RequestMapping("/discount")
public class DiscountController {

    private final DiscountStrategyFactory discountStrategyFactory;

    public DiscountController(DiscountStrategyFactory discountStrategyFactory) {
        this.discountStrategyFactory = discountStrategyFactory;
    }

    /**
     * type: 1=电子产品  2=服装  3=生鲜  4=图书
     * isNew: 电子产品=新用户  生鲜=当日采摘  图书=新书
     * vLevel: 会员等级 0/1/2/3+
     * isHol: 是否节假日
     */
    @Operation(summary = "计算大促最终成交价")
    @GetMapping("/calculate")
    public ResponseEntity<?> calculate(
            @RequestParam double price,
            @RequestParam int type,
            @RequestParam(defaultValue = "false") boolean isNew,
            @RequestParam(defaultValue = "0") int vLevel,
            @RequestParam(defaultValue = "false") boolean isHol) {
        
        IDiscountStrategy strategy = discountStrategyFactory.getStrategy(type);
        
        if (strategy == null) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "不支持的商品类型: " + type);
            return ResponseEntity.badRequest().body(error);
        }
        
        double finalPrice = strategy.calculateDiscount(price, isNew, vLevel, isHol);
        
        if (isHol && finalPrice >= 300) {
            if (type != 3) {
                finalPrice = finalPrice * 0.9;
            }
        }
        
        if (vLevel >= 3 && isHol) {
            finalPrice = finalPrice - 10;
        }
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("originalPrice", price);
        result.put("finalPrice", Math.max(finalPrice, 0));
        result.put("discount", price > 0
                ? Math.round((1 - Math.max(finalPrice, 0) / price) * 10000d) / 100d : 0);
        return ResponseEntity.ok(result);
    }

    // Deleted:    public double calculateFinalPrice(double price, int type, boolean isNew, int vLevel, boolean isHol) {
    // Deleted:        double fPrice = price;
    // Deleted:        if (type == 1) { // 1代表电子产品
    // Deleted:            if (isNew) {
    // Deleted:                fPrice = price - 50;
    // Deleted:                if (fPrice < 0) fPrice = 0;
    // Deleted:            } else {
    // Deleted:                if (vLevel > 0) {
    // Deleted:                    if (vLevel == 1) { fPrice = price * 0.95; }
    // Deleted:                    else if (vLevel == 2) { fPrice = price * 0.90; }
    // Deleted:                    else if (vLevel >= 3) { fPrice = price * 0.85; }
    // Deleted:                }
    // Deleted:            }
    // Deleted:            if (isHol && fPrice > 500) {
    // Deleted:                fPrice = fPrice - 30;
    // Deleted:            }
    // Deleted:        } else if (type == 2) { // 2代表服装
    // Deleted:            if (isHol) {
    // Deleted:                if (price >= 200) {
    // Deleted:                    fPrice = price - 40;
    // Deleted:                } else {
    // Deleted:                    fPrice = price * 0.8;
    // Deleted:                }
    // Deleted:            } else {
    // Deleted:                 if (vLevel >= 2) { fPrice = price * 0.88; }
    // Deleted:            }
    // Deleted:            if (isNew && vLevel >= 1) {
    // Deleted:                fPrice = fPrice * 0.95;
    // Deleted:            }
    // Deleted:        } else if (type == 3) { // 3代表生鲜
    // Deleted:            if (isNew) {
    // Deleted:                fPrice = price * 0.7;
    // Deleted:                if (fPrice > 100) { fPrice = fPrice - 5; }
    // Deleted:            } else if (isHol) {
    // Deleted:                if (price >= 50) {
    // Deleted:                    fPrice = price - 8;
    // Deleted:                }
    // Deleted:            } else {
    // Deleted:                if (vLevel == 1) { fPrice = price * 0.98; }
    // Deleted:                else if (vLevel == 2) { fPrice = price * 0.97; }
    // Deleted:                else if (vLevel >= 3) { fPrice = price * 0.95; }
    // Deleted:            }
    // Deleted:        } else if (type == 4) { // 4代表图书
    // Deleted:            if (!isNew) {
    // Deleted:                if (isHol) {
    // Deleted:                    if (price >= 100) {
    // Deleted:                        fPrice = price - 15;
    // Deleted:                    } else {
    // Deleted:                        fPrice = price * 0.9;
    // Deleted:                    }
    // Deleted:                    if (vLevel >= 2) {
    // Deleted:                        fPrice = fPrice * 0.98;
    // Deleted:                    }
    // Deleted:                } else {
    // Deleted:                    if (vLevel >= 2) { fPrice = price * 0.95; }
    // Deleted:                    else if (vLevel == 1) { fPrice = price * 0.98; }
    // Deleted:                }
    // Deleted:            }
    // Deleted:            if (isHol && fPrice >= 200) {
    // Deleted:                fPrice = fPrice - 20;
    // Deleted:            }
    // Deleted:        }
    // Deleted:        if (isHol && fPrice >= 300) {
    // Deleted:            if (type != 3) {
    // Deleted:                fPrice = fPrice * 0.9;
    // Deleted:            }
    // Deleted:        }
    // Deleted:        if (vLevel >= 3 && isHol) {
    // Deleted:            fPrice = fPrice - 10;
    // Deleted:        }
    // Deleted:        return fPrice;
    // Deleted:    }
}
