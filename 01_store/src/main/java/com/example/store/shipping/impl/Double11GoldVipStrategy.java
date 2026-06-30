package com.example.store.shipping.impl;

import com.example.store.shipping.IShippingStrategy;
import org.springframework.stereotype.Component;

/**
 * 双十一黄金VIP运费策略
 * 规则：满199包邮，否则按地区和重量计费
 */
@Component("double11GoldVip")
public class Double11GoldVipStrategy implements IShippingStrategy {
    
    // ... existing code ...

    @Override
    public double calculateShipping(int regionCode, double weight, double orderAmount) {
        if (orderAmount > 199) {
            return 0.0; // 满199包邮
        } else {
            if (regionCode == 1 || regionCode == 2 || regionCode == 3) {
                // 江浙沪
                if (weight > 1.0) {
                    return 6.0 + (weight - 1.0) * 2.0;
                } else {
                    return 6.0;
                }
            } else {
                if (regionCode == 89 || regionCode == 90) {
                    // 新疆西藏
                    return 25.0 + weight * 6.0;
                } else {
                    // 其他地区
                    return 15.0 + weight * 4.0;
                }
            }
        }
    }

    // Deleted:    @Override
    // Deleted:    public double calculateShipping(double weight, double orderAmount, int userVipLevel, boolean isNewUser) {
    // Deleted:        // TODO Auto-generated method stub
    // Deleted:        throw new UnsupportedOperationException("Unimplemented method 'calculateShipping'");
    // Deleted:    }
}