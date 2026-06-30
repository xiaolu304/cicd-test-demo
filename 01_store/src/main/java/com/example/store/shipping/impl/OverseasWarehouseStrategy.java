package com.example.store.shipping.impl;

import com.example.store.shipping.IShippingStrategy;
import org.springframework.stereotype.Component;

@Component("overseasWarehouse")
public class OverseasWarehouseStrategy implements IShippingStrategy {

    @Override
    public double calculateShipping(int regionCode, double weight, double orderAmount) {
        // 固定为钻石会员、非新用户，后续可以在Controller里扩展
        int userVipLevel = 3;
        boolean isNewUser = false;

        // 1. 计算基础运费（包含80元起步价）
        double baseCost = calculateBaseCost(weight);

        // 2. 应用VIP折扣
        baseCost = applyVipDiscount(baseCost, userVipLevel);

        // 3. 应用订单金额优惠（满500减30）
        baseCost = applyOrderAmountDiscount(baseCost, orderAmount);

        // 4. 应用新用户优惠（再减20）
        baseCost = applyNewUserDiscount(baseCost, isNewUser);

        return Math.max(baseCost, 0);
    }

    private double calculateBaseCost(double weight) {
        double fee = 80;
        if (weight <= 0) {
            return fee;
        } else if (weight <= 5) {
            fee += weight * 25;
        } else if (weight <= 20) {
            fee += 5 * 25;
            fee += (weight - 5) * 20;
        } else {
            fee += 5 * 25;
            fee += 15 * 20;
            fee += (weight - 20) * 20;
        }
        return fee;
    }

    public double applyVipDiscount(double cost, int vipLevel) {
        switch (vipLevel) {
            case 3: return cost * 0.7; // 钻石7折
            case 2: return cost * 0.8; // 黄金8折
            default: return cost;
        }
    }

    public double applyOrderAmountDiscount(double cost, double orderAmount) {
        return orderAmount >= 500 ? cost - 30 : cost;
    }

    public double applyNewUserDiscount(double cost, boolean isNewUser) {
        return isNewUser ? cost - 20 : cost;
    }
}