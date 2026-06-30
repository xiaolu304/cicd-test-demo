package com.example.store.shipping.impl;

import com.example.store.shipping.IShippingStrategy;
import org.springframework.stereotype.Component;

/**
 * 平时黄金VIP运费策略
 * 规则：满299包邮，否则按地区重量计费
 */
@Component("normalGoldVip")
public class NormalGoldVipStrategy implements IShippingStrategy {
    
    // ... existing code ...

    @Override
    public double calculateShipping(int regionCode, double weight, double orderAmount) {
        if (orderAmount > 299) {
            return 0.0; // 满299包邮
        } else {
            if (regionCode == 89 || regionCode == 90) {
                // 新疆西藏
                return 28.0 + weight * 12.0;
            } else {
                // 其他地区
                return 12.0 + weight * 4.0;
            }
        }
    }

    // Deleted:    @Override
    // Deleted:    public double calculateShipping(double weight, double orderAmount, int userVipLevel, boolean isNewUser) {
    // Deleted:        // TODO Auto-generated method stub
    // Deleted:        throw new UnsupportedOperationException("Unimplemented method 'calculateShipping'");
    // Deleted:    }
}