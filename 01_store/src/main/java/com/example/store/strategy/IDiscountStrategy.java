package com.example.store.strategy;

/**
 * 折扣策略接口
 * 所有折扣策略都必须实现此接口
 */
public interface IDiscountStrategy {
    
    /**
     * 计算折扣后的价格
     * @param originalPrice 原始价格
     * @param isNew 是否新用户/新品/新采摘等（根据不同商品类型含义不同）
     * @param vLevel 会员等级 0/1/2/3+
     * @param isHoliday 是否节假日
     * @return 折扣后的最终价格
     */
    double calculateDiscount(double originalPrice, boolean isNew, int vLevel, boolean isHoliday);
}