package com.example.store.shipping.impl;

import com.example.store.shipping.IShippingStrategy;
import org.springframework.stereotype.Component;

/**
 * 双十一普通用户运费策略
 * 规则：偏远地区特殊计费，其他地区满299包邮或按地区重量计费
 */
@Component("double11NormalUser")
public class Double11NormalUserStrategy implements IShippingStrategy {
    
    // ... existing code ...

    @Override
    public double calculateShipping(int regionCode, double weight, double orderAmount) {
        boolean isRemoteArea = (regionCode == 89 || regionCode == 90);
        
        if (isRemoteArea) {
            // 偏远地区（新疆西藏）
            return 30.0 + weight * 15.0;
        } else {
            if (orderAmount > 299) {
                return 0.0; // 满299包邮
            } else {
                if (regionCode == 1 || regionCode == 2 || regionCode == 3) {
                    // 江浙沪
                    return 10.0 + weight * 3.0;
                } else {
                    // 其他地区
                    return 12.0 + weight * 5.0;
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