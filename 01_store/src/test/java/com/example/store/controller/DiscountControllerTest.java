package com.example.store.controller;

import com.example.store.strategy.DiscountStrategyFactory;
import com.example.store.strategy.IDiscountStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * DiscountController 单元测试
 * 
 * 测试目标：验证折扣计算接口的各种场景
 * 测试原则：3A原则 (Arrange-Act-Assert)
 * Mock策略：完全模拟DiscountStrategyFactory和IDiscountStrategy，不依赖真实实现
 */
@DisplayName("大促折扣计算控制器测试")
class DiscountControllerTest {

    private DiscountController discountController;
    private DiscountStrategyFactory mockFactory;
    private IDiscountStrategy mockStrategy;

    /**
     * Arrange: 每个测试方法执行前的准备工作
     * 创建Mock对象并注入到Controller中
     */
    @BeforeEach
    void setUp() {
        // 创建Mock对象
        mockFactory = mock(DiscountStrategyFactory.class);
        mockStrategy = mock(IDiscountStrategy.class);
        
        // 将Mock工厂注入到Controller
        discountController = new DiscountController(mockFactory);
    }

    // ==================== 正向路径测试 ====================

    /**
     * 测试用例1：电子产品新用户优惠 - 正常流程
     * 
     * 场景：购买电子产品，新用户，非节假日，普通用户
     * 预期：返回正确的折扣后价格
     */
    @Test
    @DisplayName("TC01-电子产品新用户-正常折扣计算")
    void testElectronicsNewUserNormalFlow() {
        // Arrange: 准备测试数据
        double originalPrice = 500.0;
        int productType = 1; // 电子产品
        boolean isNewUser = true;
        int vipLevel = 0;
        boolean isHoliday = false;
        double expectedDiscountedPrice = 450.0; // 假设策略返回450
        
        // 配置Mock行为：当调用getStrategy(1)时返回mockStrategy
        when(mockFactory.getStrategy(productType)).thenReturn(mockStrategy);
        // 配置Mock行为：当调用calculateDiscount时返回预期价格
        when(mockStrategy.calculateDiscount(originalPrice, isNewUser, vipLevel, isHoliday))
                .thenReturn(expectedDiscountedPrice);

        // Act: 执行被测试的方法
        ResponseEntity<?> response = discountController.calculate(
                originalPrice, productType, isNewUser, vipLevel, isHoliday);

        // Assert: 验证结果
        assertEquals(200, response.getStatusCodeValue(), "HTTP状态码应为200");
        assertNotNull(response.getBody(), "响应体不应为空");
        
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(originalPrice, body.get("originalPrice"), "原价应正确返回");
        assertEquals(expectedDiscountedPrice, body.get("finalPrice"), "最终价格应与策略返回值一致");
        
        // 验证Mock方法被正确调用
        verify(mockFactory, times(1)).getStrategy(productType);
        verify(mockStrategy, times(1)).calculateDiscount(originalPrice, isNewUser, vipLevel, isHoliday);
    }

    /**
     * 测试用例2：服装类商品节假日满减 - 正常流程
     * 
     * 场景：购买服装，老用户，节假日，黄金VIP
     * 预期：应用节假日折扣和VIP折扣
     */
    @Test
    @DisplayName("TC02-服装节假日-VIP折扣叠加")
    void testClothingHolidayWithVip() {
        // Arrange
        double originalPrice = 350.0;
        int productType = 2; // 服装
        boolean isNewUser = false;
        int vipLevel = 2; // 黄金VIP
        boolean isHoliday = true;
        double strategyPrice = 310.0; // 策略计算后的价格
        double expectedFinalPrice = 279.0; // 310 * 0.9 = 279 (节假日满300打9折)
        
        when(mockFactory.getStrategy(productType)).thenReturn(mockStrategy);
        when(mockStrategy.calculateDiscount(originalPrice, isNewUser, vipLevel, isHoliday))
                .thenReturn(strategyPrice);

        // Act
        ResponseEntity<?> response = discountController.calculate(
                originalPrice, productType, isNewUser, vipLevel, isHoliday);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        
        // 验证最终价格：先应用策略折扣，再应用节假日9折
        assertEquals(expectedFinalPrice, body.get("finalPrice"), 
                "应正确应用节假日9折优惠（满300）");
        
        // 验证折扣率计算正确
        double expectedDiscount = Math.round((1 - expectedFinalPrice / originalPrice) * 10000d) / 100d;
        assertEquals(expectedDiscount, body.get("discount"), "折扣率计算应正确");
    }

    // ==================== 边缘条件测试 ====================

    /**
     * 测试用例3：不支持的商品类型 - 异常路径
     * 
     * 场景：传入无效的商品类型（如type=99）
     * 预期：返回400错误和友好的错误信息
     */
    @Test
    @DisplayName("TC03-无效商品类型-返回错误响应")
    void testInvalidProductType() {
        // Arrange
        double originalPrice = 100.0;
        int invalidType = 99; // 无效的商品类型
        
        // 配置Mock：返回null表示不支持该类型
        when(mockFactory.getStrategy(invalidType)).thenReturn(null);

        // Act
        ResponseEntity<?> response = discountController.calculate(
                originalPrice, invalidType, false, 0, false);

        // Assert
        assertEquals(400, response.getStatusCodeValue(), "应返回400 Bad Request");
        assertNotNull(response.getBody(), "响应体不应为空");
        
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(body.containsKey("error"), "错误响应应包含error字段");
        assertTrue(((String) body.get("error")).contains("99"), 
                "错误信息应包含无效的类型值");
        
        // 验证不会调用策略的calculateDiscount方法
        verify(mockStrategy, never()).calculateDiscount(anyDouble(), anyBoolean(), anyInt(), anyBoolean());
    }

    /**
     * 测试用例4：价格为0或负数 - 边界条件
     * 
     * 场景：传入0或负数的价格
     * 预期：系统能正确处理，不会出现除以零等异常
     */
    @Test
    @DisplayName("TC04-零价格商品-边界值处理")
    void testZeroPrice() {
        // Arrange
        double zeroPrice = 0.0;
        int productType = 1;
        
        when(mockFactory.getStrategy(productType)).thenReturn(mockStrategy);
        when(mockStrategy.calculateDiscount(zeroPrice, false, 0, false))
                .thenReturn(0.0);

        // Act
        ResponseEntity<?> response = discountController.calculate(
                zeroPrice, productType, false, 0, false);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(0.0, body.get("originalPrice"));
        assertEquals(0.0, body.get("finalPrice"));
        assertEquals(0.0, body.get("discount"), "价格为0时折扣率应为0");
    }

    /**
     * 测试用例5：钻石VIP节假日额外减免 - 复杂业务逻辑
     * 
     * 场景：钻石VIP（level>=3）在节假日购物
     * 预期：除了常规折扣外，还享受额外减10元优惠
     */
    @Test
    @DisplayName("TC05-钻石VIP节假日-额外减免10元")
    void testDiamondVipHolidayExtraDiscount() {
        // Arrange
        double originalPrice = 400.0;
        int productType = 1; // 电子产品
        boolean isNewUser = false;
        int vipLevel = 3; // 钻石VIP
        boolean isHoliday = true;
        double strategyPrice = 340.0; // 策略计算后价格
        double afterHolidayDiscount = 306.0; // 340 * 0.9 = 306
        double expectedFinalPrice = 296.0; // 306 - 10 = 296 (钻石VIP额外减10)
        
        when(mockFactory.getStrategy(productType)).thenReturn(mockStrategy);
        when(mockStrategy.calculateDiscount(originalPrice, isNewUser, vipLevel, isHoliday))
                .thenReturn(strategyPrice);

        // Act
        ResponseEntity<?> response = discountController.calculate(
                originalPrice, productType, isNewUser, vipLevel, isHoliday);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(expectedFinalPrice, body.get("finalPrice"), 
                "钻石VIP在节假日应享受额外减10元优惠");
    }

    /**
     * 测试用例6：生鲜商品节假日不打9折 - 特殊规则
     * 
     * 场景：生鲜商品（type=3）即使满300也不应用节假日9折
     * 预期：只应用策略折扣，不应用额外的9折
     */
    @Test
    @DisplayName("TC06-生鲜商品-节假日例外规则")
    void testFreshProductNoHolidayDiscount() {
        // Arrange
        double originalPrice = 350.0;
        int productType = 3; // 生鲜
        boolean isNewUser = false;
        int vipLevel = 0;
        boolean isHoliday = true;
        double strategyPrice = 320.0;
        // 注意：生鲜商品即使满300也不打9折，所以最终价格就是strategyPrice
        
        when(mockFactory.getStrategy(productType)).thenReturn(mockStrategy);
        when(mockStrategy.calculateDiscount(originalPrice, isNewUser, vipLevel, isHoliday))
                .thenReturn(strategyPrice);

        // Act
        ResponseEntity<?> response = discountController.calculate(
                originalPrice, productType, isNewUser, vipLevel, isHoliday);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(strategyPrice, body.get("finalPrice"), 
                "生鲜商品不应应用节假日9折优惠");
    }

    /**
     * 测试用例7：最终价格为负数的保护 - 防御性编程
     * 
     * 场景：策略返回的价格可能因大量折扣变为负数
     * 预期：系统应确保最终价格不低于0
     */
    @Test
    @DisplayName("TC07-负价格保护-Math.max兜底")
    void testNegativePriceProtection() {
        // Arrange
        double originalPrice = 50.0;
        int productType = 1;
        boolean isNewUser = true;
        int vipLevel = 3; // 钻石VIP
        boolean isHoliday = true;
        double negativeStrategyPrice = -10.0; // 策略返回负数（异常情况）
        
        when(mockFactory.getStrategy(productType)).thenReturn(mockStrategy);
        when(mockStrategy.calculateDiscount(originalPrice, isNewUser, vipLevel, isHoliday))
                .thenReturn(negativeStrategyPrice);

        // Act
        ResponseEntity<?> response = discountController.calculate(
                originalPrice, productType, isNewUser, vipLevel, isHoliday);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(0.0, body.get("finalPrice"), 
                "最终价格不应为负数，Math.max应将其修正为0");
    }
}