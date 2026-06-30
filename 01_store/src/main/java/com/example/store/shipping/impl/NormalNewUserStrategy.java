package com.example.store.shipping.impl;

import com.example.store.shipping.IShippingStrategy;
import org.springframework.stereotype.Component;

/**
 * 平时新用户运费策略
 * 规则：满88包邮，否则按地区重量计费
 */
@Component("normalNewUser")
public class NormalNewUserStrategy implements IShippingStrategy {
    
   // ... existing code ...

    @Override
    public double calculateShipping(int regionCode, double weight, double orderAmount) {
        if (orderAmount > 88) {
            return 0.0; // 满88包邮
        } else {
            if (regionCode == 89 || regionCode == 90) {
                // 新疆西藏
                return 30.0 + weight * 12.0;
            } else {
                // 其他地区
                return 10.0 + weight * 3.0;
            }
        }
    }

    // Deleted:    @Override
    // Deleted:    public double calculateShipping(double weight, double orderAmount, int userVipLevel, boolean isNewUser) {
    // Deleted:        // TODO Auto-generated method stub
    // Deleted:        throw new UnsupportedOperationException("Unimplemented method 'calculateShipping'");
    // Deleted:    }
}