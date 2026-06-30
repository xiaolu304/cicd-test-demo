package com.example.store.strategy; // 1. 修正包路径

import com.example.store.strategy.impl.*; // 导入所有策略实现类
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 折扣策略工厂
 * 根据商品类型获取对应的折扣策略
 */
@Component
public class DiscountStrategyFactory {
    
    private final Map<Integer, IDiscountStrategy> strategyMap = new HashMap<>();
    
    public DiscountStrategyFactory(ElectronicsDiscountStrategy electronicsDiscount,
                                   ClothingDiscountStrategy clothingDiscount,
                                   FreshDiscountStrategy freshDiscount,
                                   BookDiscountStrategy bookDiscount) {
        strategyMap.put(1, electronicsDiscount); // 电子产品
        strategyMap.put(2, clothingDiscount);    // 服装
        strategyMap.put(3, freshDiscount);       // 生鲜
        strategyMap.put(4, bookDiscount);        // 图书
    }
    
    /**
     * 根据商品类型获取折扣策略
     * @param productType 商品类型：1=电子产品 2=服装 3=生鲜 4=图书
     * @return 对应的折扣策略，如果类型不存在则返回null
     */
    public IDiscountStrategy getStrategy(int productType) {
        return strategyMap.get(productType);
    }
}