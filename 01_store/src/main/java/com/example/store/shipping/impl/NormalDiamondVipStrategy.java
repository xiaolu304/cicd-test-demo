package com.example.store.shipping.impl;

import com.example.store.shipping.IShippingStrategy;
import org.springframework.stereotype.Component;

/**
 * 平时钻石VIP运费策略
 * 规则：满199包邮，否则按地区重量计费
 */
@Component("normalDiamondVip")
public class NormalDiamondVipStrategy implements IShippingStrategy {
    
    // ... existing code ...

    @Override
    public double calculateShipping(int regionCode, double weight, double orderAmount) {
        if (orderAmount > 199) {
            return 0.0; // 满199包邮
        } else {
            if (regionCode == 89 || regionCode == 90) {
                // 新疆西藏
                return 20.0 + weight * 10.0;
            } else {
                // 其他地区
                return 8.0 + weight * 2.0;
            }
        }
    }

    // Deleted:    @Override
    // Deleted:    public double calculateShipping(double weight, double orderAmount, int userVipLevel, boolean isNewUser) {
    // Deleted:        // TODO Auto-generated method stub
    // Deleted:        throw new UnsupportedOperationException("Unimplemented method 'calculateShipping'");
    // Deleted:    }
}