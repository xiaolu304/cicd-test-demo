package com.example.store.controller;

import com.example.store.shipping.IShippingStrategy;
import com.example.store.shipping.ShippingStrategyFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "双十一运费计算")
@RestController
@RequestMapping("/shipping")
public class ShippingController {

    private final ShippingStrategyFactory shippingStrategyFactory;

    public ShippingController(ShippingStrategyFactory shippingStrategyFactory) {
        this.shippingStrategyFactory = shippingStrategyFactory;
    }

    /**
     * regionCode: 地区代码 (1,2,3 好像是江浙沪, 89,90 是新疆西藏，其他随便猜)
     * userVipLevel: 用户等级 (3:钻石, 2:黄金, 1或0:普通)
     * weight: 订单总重量 (kg)
     * orderAmount: 订单总金额
     * isDouble11: 是否双十一期间
     * isNewUser: 是否新用户
     */
    @Operation(summary = "计算订单最终运费")
    @GetMapping("/calculate")
    public ResponseEntity<?> calculate(
            @RequestParam int regionCode,
            @RequestParam(defaultValue = "0") int userVipLevel,
            @RequestParam double weight,
            @RequestParam double orderAmount,
            @RequestParam(defaultValue = "false") boolean isDouble11,
            @RequestParam(defaultValue = "false") boolean isNewUser) {
        
        // ✨ 第 1 行：通过工厂获取对应策略
        IShippingStrategy strategy = shippingStrategyFactory.getStrategy(isDouble11, isNewUser, userVipLevel);
        
        // ✨ 第 2 行：调用策略计算运费
        double finalCost = strategy.calculateShipping(regionCode, weight, orderAmount);
        
        // 双十一统一打9折（保留原有逻辑）
        if (isDouble11 && finalCost > 0) {
            finalCost = finalCost * 0.9;
        }
        
        // 封装返回结果
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderAmount", orderAmount);
        result.put("weight", weight);
        result.put("regionCode", regionCode);
        result.put("isDouble11", isDouble11);
        result.put("finalShippingCost", Math.max(finalCost, 0));
        
        return ResponseEntity.ok(result);
    }

    // Deleted:    // TODO: 2019-11-10 王建国: 临时加的，双十一后重构（绝对不能删！！！）
    // Deleted:    // TODO: 2021-11-08 李小明: 逻辑太复杂了，不敢动，加了几个if
    // Deleted:    // TODO: 2023-10-25 张伟: 增加偏远地区运费特殊逻辑，听天由命吧，跑通就行
    // Deleted:    public double calculateShippingCost(int regionCode, int userVipLevel, double weight, double orderAmount, boolean isDouble11, boolean isNewUser) {
    // Deleted:        double finalCost = 0.0;
    // Deleted:        int tempFlag = 0; // 别问我干嘛用的，上一个离职的人写的，去掉就报错
    // Deleted:
    // Deleted:        if (isDouble11) {
    // Deleted:            if (isNewUser) {
    // Deleted:                if (orderAmount > 50) {
    // Deleted:                    finalCost = 0.0; // 双十一新用户满50包邮
    // Deleted:                } else {
    // Deleted:                    if (regionCode == 1 || regionCode == 2 || regionCode == 3) { 
    // Deleted:                        finalCost = 5.0;
    // Deleted:                    } else {
    // Deleted:                        if (regionCode == 89 || regionCode == 90) { 
    // Deleted:                            finalCost = 25.0;
    // Deleted:                        } else {
    // Deleted:                            finalCost = 12.0;
    // Deleted:                        }
    // Deleted:                    }
    // Deleted:                }
    // Deleted:            } else { // 老用户
    // Deleted:                if (userVipLevel == 3) { // 顶级VIP (钻石？)
    // Deleted:                    if (regionCode == 89 || regionCode == 90) {
    // Deleted:                        if (weight <= 2.0) {
    // Deleted:                            finalCost = 15.0;
    // Deleted:                        } else {
    // Deleted:                            finalCost = 15.0 + (weight - 2.0) * 8.5; // 超过2kg按8.5算
    // Deleted:                        }
    // Deleted:                    } else {
    // Deleted:                        if (orderAmount > 99) {
    // Deleted:                            finalCost = 0.0; 
    // Deleted:                        } else {
    // Deleted:                            finalCost = weight * 1.5; 
    // Deleted:                            if (finalCost < 5.0) {
    // Deleted:                                finalCost = 5.0; // 最低5块
    // Deleted:                            }
    // Deleted:                        }
    // Deleted:                    }
    // Deleted:                } else if (userVipLevel == 2) { // 黄金VIP
    // Deleted:                    if (orderAmount > 199) {
    // Deleted:                        finalCost = 0.0;
    // Deleted:                    } else {
    // Deleted:                        if (regionCode == 1 || regionCode == 2 || regionCode == 3) {
    // Deleted:                            if (weight > 1.0) {
    // Deleted:                                finalCost = 6.0 + (weight - 1.0) * 2.0;
    // Deleted:                            } else {
    // Deleted:                                finalCost = 6.0;
    // Deleted:                            }
    // Deleted:                        } else {
    // Deleted:                            if (regionCode == 89 || regionCode == 90) {
    // Deleted:                                finalCost = 25.0 + weight * 6.0;
    // Deleted:                            } else {
    // Deleted:                                finalCost = 15.0 + weight * 4.0;
    // Deleted:                            }
    // Deleted:                        }
    // Deleted:                    }
    // Deleted:                } else { // 普通老用户
    // Deleted:                    if (regionCode == 89 || regionCode == 90) {
    // Deleted:                        tempFlag = 1;
    // Deleted:                    }
    // Deleted:                    if (tempFlag == 1) {
    // Deleted:                        finalCost = 30.0 + weight * 15.0; // 偏远地区普通用户原价
    // Deleted:                    } else {
    // Deleted:                        if (orderAmount > 299) {
    // Deleted:                            finalCost = 0.0;
    // Deleted:                        } else {
    // Deleted:                            if (regionCode == 1 || regionCode == 2 || regionCode == 3) {
    // Deleted:                                finalCost = 10.0 + weight * 3.0;
    // Deleted:                            } else {
    // Deleted:                                finalCost = 12.0 + weight * 5.0;
    // Deleted:                            }
    // Deleted:                        }
    // Deleted:                    }
    // Deleted:                }
    // Deleted:            }
    // Deleted:        } else {
    // Deleted:            // 平时的运费逻辑 (王建国当年直接Ctrl+C Ctrl+V，改了几个数字)
    // Deleted:            if (isNewUser) {
    // Deleted:                if (orderAmount > 88) { // 平时新用户满88才包邮
    // Deleted:                    finalCost = 0.0;
    // Deleted:                } else {
    // Deleted:                    if (regionCode == 89 || regionCode == 90) {
    // Deleted:                        finalCost = 30.0 + weight * 12.0;
    // Deleted:                    } else {
    // Deleted:                        finalCost = 10.0 + weight * 3.0;
    // Deleted:                    }
    // Deleted:                }
    // Deleted:            } else { // 平时老用户
    // Deleted:                if (userVipLevel == 3) {
    // Deleted:                     if (orderAmount > 199) { 
    // Deleted:                         finalCost = 0.0;
    // Deleted:                     } else {
    // Deleted:                         if (regionCode == 89 || regionCode == 90) {
    // Deleted:                             finalCost = 20.0 + weight * 10.0;
    // Deleted:                         } else {
    // Deleted:                             finalCost = 8.0 + weight * 2.0;
    // Deleted:                         }
    // Deleted:                     }
    // Deleted:                } else if (userVipLevel == 2) {
    // Deleted:                    if (orderAmount > 299) {
    // Deleted:                        finalCost = 0.0;
    // Deleted:                    } else {
    // Deleted:                         if (regionCode == 89 || regionCode == 90) {
    // Deleted:                             finalCost = 28.0 + weight * 12.0;
    // Deleted:                         } else {
    // Deleted:                             finalCost = 12.0 + weight * 4.0;
    // Deleted:                         }
    // Deleted:                    }
    // Deleted:                } else { // 平时普通老用户
    // Deleted:                    if (regionCode == 89 || regionCode == 90) {
    // Deleted:                        finalCost = 35.0 + weight * 18.0;
    // Deleted:                    } else {
    // Deleted:                        if (regionCode == 1 || regionCode == 2 || regionCode == 3) {
    // Deleted:                            finalCost = 12.0 + weight * 5.0;
    // Deleted:                        } else {
    // Deleted:                            finalCost = 15.0 + weight * 6.0;
    // Deleted:                        }
    // Deleted:                    }
    // Deleted:                }
    // Deleted:            }
    // Deleted:        }
    // Deleted:
    // Deleted:        // 临时打个补丁：如果是双十一，不知道为什么当时产品要求统一打个9折
    // Deleted:        // TODO: 记得下掉这个逻辑！(留言时间 2020年)
    // Deleted:        if (isDouble11 && finalCost > 0) {
    // Deleted:            finalCost = finalCost * 0.9;
    // Deleted:        }
    // Deleted:
    // Deleted:        return finalCost;
    // Deleted:    }
}