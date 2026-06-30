package com.example.store.shipping.impl;

import com.example.store.shipping.IShippingStrategy;
import org.springframework.stereotype.Component;

/**
 * 双十一新用户运费策略
 * 规则：满50包邮，否则按地区收费
 */
@Component("double11NewUser")
public class Double11NewUserStrategy implements IShippingStrategy {
    
    @Override
    public double calculateShipping(int regionCode, double weight, double orderAmount) {
        if (orderAmount > 50) {
            return 0.0; // 满50包邮
        } else {
            if (regionCode == 1 || regionCode == 2 || regionCode == 3) {
                return 5.0; // 江浙沪
            } else {
                if (regionCode == 89 || regionCode == 90) {
                    return 25.0; // 新疆西藏
                } else {
                    return 12.0; // 其他地区
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