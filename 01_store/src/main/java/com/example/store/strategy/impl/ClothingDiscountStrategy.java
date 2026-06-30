package com.example.store.strategy.impl;

import com.example.store.strategy.IDiscountStrategy;
import org.springframework.stereotype.Component;

/**
 * 服装折扣策略
 */
@Component("clothingDiscount")
public class ClothingDiscountStrategy implements IDiscountStrategy {
    
    @Override
    public double calculateDiscount(double originalPrice, boolean isNew, int vLevel, boolean isHoliday) {
        double finalPrice = originalPrice;
        
        if (isHoliday) {
            if (originalPrice >= 200) {
                finalPrice = originalPrice - 40;
            } else {
                finalPrice = originalPrice * 0.8;
            }
        } else {
            if (vLevel >= 2) { 
                finalPrice = originalPrice * 0.88; 
            }
        }
        
        if (isNew && vLevel >= 1) {
            finalPrice = finalPrice * 0.95;
        }
        
        return finalPrice;
    }
}