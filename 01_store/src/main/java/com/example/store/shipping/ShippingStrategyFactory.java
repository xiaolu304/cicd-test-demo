package com.example.store.shipping;

import com.example.store.shipping.impl.*;
import org.springframework.stereotype.Component;

/**
 * 运费策略工厂
 * 根据是否双十一、是否新用户、VIP等级获取对应的运费策略
 */
@Component
public class ShippingStrategyFactory {
    
    private final IShippingStrategy double11NewUser;
    private final IShippingStrategy double11DiamondVip;
    private final IShippingStrategy double11GoldVip;
    private final IShippingStrategy double11NormalUser;
    private final IShippingStrategy normalNewUser;
    private final IShippingStrategy normalDiamondVip;
    private final IShippingStrategy normalGoldVip;
    private final IShippingStrategy normalNormalUser;
    private final IShippingStrategy overseasWarehouse;
    
    public ShippingStrategyFactory(
            Double11NewUserStrategy double11NewUser,
            Double11DiamondVipStrategy double11DiamondVip,
            Double11GoldVipStrategy double11GoldVip,
            Double11NormalUserStrategy double11NormalUser,
            NormalNewUserStrategy normalNewUser,
            NormalDiamondVipStrategy normalDiamondVip,
            NormalGoldVipStrategy normalGoldVip,
            NormalNormalUserStrategy normalNormalUser,
            OverseasWarehouseStrategy overseasWarehouse) {
        this.double11NewUser = double11NewUser;
        this.double11DiamondVip = double11DiamondVip;
        this.double11GoldVip = double11GoldVip;
        this.double11NormalUser = double11NormalUser;
        this.normalNewUser = normalNewUser;
        this.normalDiamondVip = normalDiamondVip;
        this.normalGoldVip = normalGoldVip;
        this.normalNormalUser = normalNormalUser;
        this.overseasWarehouse = overseasWarehouse;
    }
    
    /**
     * 根据条件获取运费策略
     * @param isDouble11 是否双十一
     * @param isNewUser 是否新用户
     * @param userVipLevel VIP等级 (3=钻石, 2=黄金, 0/1=普通)
     * @return 对应的运费策略
     */
    public IShippingStrategy getStrategy(boolean isDouble11, boolean isNewUser, int userVipLevel) {
        if (isDouble11) {
            if (isNewUser) {
                return double11NewUser;
            } else {
                switch (userVipLevel) {
                    case 3: return double11DiamondVip;
                    case 2: return double11GoldVip;
                    default: return double11NormalUser;
                }
            }
        } else {
            if (isNewUser) {
                return normalNewUser;
            } else {
                switch (userVipLevel) {
                    case 3: return normalDiamondVip;
                    case 2: return normalGoldVip;
                    default: return normalNormalUser;
                }
            }
        }
    }
    
    /**
     * 获取海外仓配送策略
     * @return 海外仓配送策略
     */
    public IShippingStrategy getOverseasWarehouseStrategy() {
        return overseasWarehouse;
    }
}