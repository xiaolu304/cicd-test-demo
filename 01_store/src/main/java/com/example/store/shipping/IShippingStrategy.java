package com.example.store.shipping;

/**
 * 运费计算策略接口
 * 所有运费计算策略都必须实现此接口
 */
public interface IShippingStrategy {
    
    /**
     * 计算运费
     * @param regionCode 地区代码 (1,2,3=江浙沪, 89,90=新疆西藏, 其他=其他地区)
     * @param weight 订单重量(kg)
     * @param orderAmount 订单金额
     * @return 运费金额
     */
    double calculateShipping(int regionCode, double weight, double orderAmount);
}