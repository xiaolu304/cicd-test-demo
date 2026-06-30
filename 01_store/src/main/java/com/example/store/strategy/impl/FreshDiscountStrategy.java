package com.example.store.strategy.impl;

import com.example.store.strategy.IDiscountStrategy;
import org.springframework.stereotype.Component;

/**
 * 生鲜折扣策略
 */
@Component("freshDiscount")
public class FreshDiscountStrategy implements IDiscountStrategy {
    
    @Override
    public double calculateDiscount(double originalPrice, boolean isNew, int vLevel, boolean isHoliday) {
        double finalPrice = originalPrice;
        
        if (isNew) {
            finalPrice = originalPrice * 0.7;
            if (finalPrice > 100) { 
                finalPrice = finalPrice - 5; 
            }
        } else if (isHoliday) {
            if (originalPrice >= 50) {
                finalPrice = originalPrice - 8;
            }
        } else {
            if (vLevel == 1) { 
                finalPrice = originalPrice * 0.98; 
            } else if (vLevel == 2) { 
                finalPrice = originalPrice * 0.97; 
            } else if (vLevel >= 3) { 
                finalPrice = originalPrice * 0.95; 
            }
        }
        
        return finalPrice;
    }
}