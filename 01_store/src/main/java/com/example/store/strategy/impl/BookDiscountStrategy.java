package com.example.store.strategy.impl;

import com.example.store.strategy.IDiscountStrategy;
import org.springframework.stereotype.Component;

/**
 * 图书折扣策略
 */
@Component("bookDiscount")
public class BookDiscountStrategy implements IDiscountStrategy {
    
    @Override
    public double calculateDiscount(double originalPrice, boolean isNew, int vLevel, boolean isHoliday) {
        double finalPrice = originalPrice;
        
        if (!isNew) {
            if (isHoliday) {
                if (originalPrice >= 100) {
                    finalPrice = originalPrice - 15;
                } else {
                    finalPrice = originalPrice * 0.9;
                }
                if (vLevel >= 2) {
                    finalPrice = finalPrice * 0.98;
                }
            } else {
                if (vLevel >= 2) { 
                    finalPrice = originalPrice * 0.95; 
                } else if (vLevel == 1) { 
                    finalPrice = originalPrice * 0.98; 
                }
            }
        }
        
        if (isHoliday && finalPrice >= 200) {
            finalPrice = finalPrice - 20;
        }
        
        return finalPrice;
    }
}