package com.example.store.strategy.impl;

import com.example.store.strategy.IDiscountStrategy;
import org.springframework.stereotype.Component;

/**
 * 电子产品折扣策略
 */
@Component("electronicsDiscount")
public class ElectronicsDiscountStrategy implements IDiscountStrategy {
    
    @Override
    public double calculateDiscount(double originalPrice, boolean isNew, int vLevel, boolean isHoliday) {
        double finalPrice = originalPrice;
        
        if (isNew) {
            finalPrice = originalPrice - 50;
            if (finalPrice < 0) finalPrice = 0;
        } else {
            if (vLevel > 0) {
                if (vLevel == 1) { 
                    finalPrice = originalPrice * 0.95; 
                } else if (vLevel == 2) { 
                    finalPrice = originalPrice * 0.90; 
                } else if (vLevel >= 3) { 
                    finalPrice = originalPrice * 0.85; 
                }
            }
        }
        
        if (isHoliday && finalPrice > 500) {
            finalPrice = finalPrice - 30;
        }
        return finalPrice;
    }
}