package com.example.store.shipping.impl;

import com.example.store.shipping.IShippingStrategy;
import org.springframework.stereotype.Component;

/**
 * 双十一钻石VIP运费策略
 * 规则：偏远地区按重量阶梯计费，其他地区满99包邮或按重量计费
 */
@Component("double11DiamondVip")
public class Double11DiamondVipStrategy implements IShippingStrategy {
    
    // ... existing code ...

    @Override
    public double calculateShipping(int regionCode, double weight, double orderAmount) {
        if (regionCode == 89 || regionCode == 90) {
            // 偏远地区（新疆西藏）
            if (weight <= 2.0) {
                return 15.0;
            } else {
                return 15.0 + (weight - 2.0) * 8.5; // 超过2kg按8.5元/kg
            }
        } else {
            // 非偏远地区
            if (orderAmount > 99) {
                return 0.0; // 满99包邮
            } else {
                double cost = weight * 1.5;
                return Math.max(cost, 5.0); // 最低5元
            }
        }
    }

    // Deleted:    @Override
    // Deleted:    public double calculateShipping(double weight, double orderAmount, int userVipLevel, boolean isNewUser) {
    // Deleted:        // TODO Auto-generated method stub
    // Deleted:        throw new UnsupportedOperationException("Unimplemented method 'calculateShipping'");
    // Deleted:    }
}
