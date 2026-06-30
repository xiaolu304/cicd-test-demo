package com.example.store.shipping.impl;

import com.example.store.shipping.IShippingStrategy;
import org.springframework.stereotype.Component;

/**
 * 平时普通用户运费策略
 * 规则：按地区和重量计费，无包邮门槛
 */
@Component("normalNormalUser")
public class NormalNormalUserStrategy implements IShippingStrategy {
    
    // ... existing code ...

    @Override
    public double calculateShipping(int regionCode, double weight, double orderAmount) {
        if (regionCode == 89 || regionCode == 90) {
            // 新疆西藏
            return 35.0 + weight * 18.0;
        } else {
            if (regionCode == 1 || regionCode == 2 || regionCode == 3) {
                // 江浙沪
                return 12.0 + weight * 5.0;
            } else {
                // 其他地区
                return 15.0 + weight * 6.0;
            }
        }
    }

    // Deleted:    @Override
    // Deleted:    public double calculateShipping(double weight, double orderAmount, int userVipLevel, boolean isNewUser) {
    // Deleted:        // TODO Auto-generated method stub
    // Deleted:        throw new UnsupportedOperationException("Unimplemented method 'calculateShipping'");
    // Deleted:    }
}