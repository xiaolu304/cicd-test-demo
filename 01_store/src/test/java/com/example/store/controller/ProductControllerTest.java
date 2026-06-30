package com.example.store.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.store.entity.Product;
import com.example.store.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * ProductController 单元测试
 * 
 * 测试目标：验证商品管理接口的各种场景（CRUD + 秒杀）
 * 测试原则：3A原则 (Arrange-Act-Assert)
 * Mock策略：完全模拟ProductService，不依赖真实数据库
 */
@DisplayName("商品管理控制器测试")
class ProductControllerTest {

    private ProductController productController;
    private ProductService mockProductService;

    /**
     * Arrange: 每个测试方法执行前的准备工作
     * 创建Mock对象并注入到Controller中
     */
    @BeforeEach
    void setUp() {
        // 创建Mock对象
        mockProductService = mock(ProductService.class);
        
        // 将Mock服务注入到Controller
        productController = new ProductController(mockProductService);
    }

    // ==================== 正向路径测试 ====================

    /**
     * 测试用例1：查询所有商品 - 正常流程
     * 
     * 场景：调用GET /api/products接口
     * 预期：返回商品列表
     */
    @Test
    @DisplayName("TC01-查询所有商品-返回列表")
    void testListAllProducts() {
        // Arrange: 准备测试数据
        List<Product> expectedProducts = Arrays.asList(
                createProduct(1L, "iPhone 15", 5999.0),
                createProduct(2L, "MacBook Pro", 12999.0)
        );
        
        // 配置Mock行为：当调用list()时返回预期列表
        when(mockProductService.list()).thenReturn(expectedProducts);

        // Act: 执行被测试的方法
        List<Product> result = productController.listAll();

        // Assert: 验证结果
        assertNotNull(result, "返回结果不应为空");
        assertEquals(2, result.size(), "应返回2个商品");
        assertEquals("iPhone 15", result.get(0).getProductName(), "第一个商品名称应正确");
        assertEquals(new BigDecimal("5999.0"), result.get(0).getPrice(), "第一个商品价格应正确");
        
        // 验证Mock方法被正确调用
        verify(mockProductService, times(1)).list();
    }

    /**
     * 测试用例2：分页查询商品 - 正常流程
     * 
     * 场景：调用GET /api/products/page?page=1&size=5
     * 预期：返回分页对象，包含当前页数据
     */
    @Test
    @DisplayName("TC02-分页查询-返回Page对象")
    void testListPageWithDefaultParams() {
        // Arrange
        Page<Product> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(
                createProduct(1L, "商品1", 100.0),
                createProduct(2L, "商品2", 200.0)
        ));
        expectedPage.setTotal(2);
        
        when(mockProductService.page(any(Page.class))).thenReturn(expectedPage);

        // Act
        Page<Product> result = productController.listPage(1, 10);

        // Assert
        assertNotNull(result, "分页结果不应为空");
        assertEquals(2, result.getRecords().size(), "当前页应有2条记录");
        assertEquals(2, result.getTotal(), "总记录数应为2");
        assertEquals(1, result.getCurrent(), "当前页码应为1");
        
        verify(mockProductService, times(1)).page(any(Page.class));
    }

    /**
     * 测试用例3：根据ID查询商品 - 正常流程
     * 
     * 场景：调用GET /api/products/1
     * 预期：返回指定ID的商品详情
     */
    @Test
    @DisplayName("TC03-根据ID查询-返回商品详情")
    void testGetByIdSuccess() {
        // Arrange
        Long productId = 1L;
        Product expectedProduct = createProduct(productId, "测试商品", 99.9);
        
        when(mockProductService.getById(productId)).thenReturn(expectedProduct);

        // Act
        Product result = productController.getById(productId);

        // Assert
        assertNotNull(result, "返回商品不应为空");
        assertEquals(productId, result.getId(), "商品ID应匹配");
        assertEquals("测试商品", result.getProductName(), "商品名称应正确");
        assertEquals(new BigDecimal("99.9"), result.getPrice(), "商品价格应正确");
        
        verify(mockProductService, times(1)).getById(productId);
    }

    /**
     * 测试用例4：新增商品 - 正常流程
     * 
     * 场景：调用POST /api/products，传入商品信息
     * 预期：保存成功，返回true
     */
    @Test
    @DisplayName("TC04-新增商品-保存成功")
    void testSaveProductSuccess() {
        // Arrange
        Product newProduct = createProduct(null, "新商品", 199.0);
        
        when(mockProductService.save(newProduct)).thenReturn(true);

        // Act
        boolean result = productController.save(newProduct);

        // Assert
        assertTrue(result, "应返回true表示保存成功");
        verify(mockProductService, times(1)).save(newProduct);
    }

    /**
     * 测试用例5：修改商品 - 正常流程
     * 
     * 场景：调用PUT /api/products/1，传入更新后的商品信息
     * 预期：更新成功，返回true，且ID被正确设置
     */
    @Test
    @DisplayName("TC05-修改商品-更新成功")
    void testUpdateProductSuccess() {
        // Arrange
        Long productId = 1L;
        Product updateProduct = createProduct(null, "更新后的商品", 299.0);
        
        when(mockProductService.updateById(any(Product.class))).thenReturn(true);

        // Act
        boolean result = productController.update(productId, updateProduct);

        // Assert
        assertTrue(result, "应返回true表示更新成功");
        assertEquals(productId, updateProduct.getId(), "商品ID应被设置为路径参数值");
        verify(mockProductService, times(1)).updateById(argThat(product -> 
                product.getId().equals(productId) && 
                product.getProductName().equals("更新后的商品")));
    }

    /**
     * 测试用例6：删除商品 - 正常流程
     * 
     * 场景：调用DELETE /api/products/1
     * 预期：删除成功，返回true
     */
    @Test
    @DisplayName("TC06-删除商品-删除成功")
    void testDeleteProductSuccess() {
        // Arrange
        Long productId = 1L;
        
        when(mockProductService.removeById(productId)).thenReturn(true);

        // Act
        boolean result = productController.delete(productId);

        // Assert
        assertTrue(result, "应返回true表示删除成功");
        verify(mockProductService, times(1)).removeById(productId);
    }

    /**
     * 测试用例7：秒杀商品 - 成功场景
     * 
     * 场景：调用GET /api/products/1/seckill
     * 预期：秒杀成功，返回"秒杀成功"
     */
    @Test
    @DisplayName("TC07-秒杀商品-成功")
    void testSeckillSuccess() {
        // Arrange
        Long productId = 1L;
        
        when(mockProductService.seckill(productId)).thenReturn(true);

        // Act
        String result = productController.seckill(productId);

        // Assert
        assertEquals("秒杀成功", result, "应返回秒杀成功消息");
        verify(mockProductService, times(1)).seckill(productId);
    }

    // ==================== 异常/边界条件测试 ====================

    /**
     * 测试用例8：查询不存在的商品 - 异常路径
     * 
     * 场景：调用GET /api/products/999，商品不存在
     * 预期：返回null
     */
    @Test
    @DisplayName("TC08-查询不存在的商品-返回null")
    void testGetByIdNotFound() {
        // Arrange
        Long nonExistentId = 999L;
        
        when(mockProductService.getById(nonExistentId)).thenReturn(null);

        // Act
        Product result = productController.getById(nonExistentId);

        // Assert
        assertNull(result, "不存在的商品应返回null");
        verify(mockProductService, times(1)).getById(nonExistentId);
    }

        /**
     * 测试用例9：分页查询 - 验证Page对象结构
     * 
     * 场景：调用分页查询接口
     * 预期：返回正确的Page对象结构
     */
    @Test
    @DisplayName("TC09-分页查询-验证Page结构")
    void testListPageWithDefaultValues() {
        // Arrange
        Page<Product> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList());
        expectedPage.setTotal(0);
        
        when(mockProductService.page(any(Page.class))).thenReturn(expectedPage);

        // Act - 传入正常的分页参数
        Page<Product> result = productController.listPage(1, 10);

        // Assert
        assertNotNull(result, "分页结果不应为空");
        assertEquals(1, result.getCurrent(), "当前页码应为1");
        assertEquals(10, result.getSize(), "每页大小应为10");
        assertEquals(0, result.getRecords().size(), "记录数应为0");
        assertEquals(0, result.getTotal(), "总记录数应为0");
        verify(mockProductService, times(1)).page(any(Page.class));
    }
    /**
     * 测试用例10：秒杀失败 - 业务异常
     * 
     * 场景：库存不足或已被抢完
     * 预期：返回"秒杀失败"
     */
    @Test
    @DisplayName("TC10-秒杀失败-库存不足")
    void testSeckillFailed() {
        // Arrange
        Long productId = 1L;
        
        when(mockProductService.seckill(productId)).thenReturn(false);

        // Act
        String result = productController.seckill(productId);

        // Assert
        assertEquals("秒杀失败", result, "应返回秒杀失败消息");
        verify(mockProductService, times(1)).seckill(productId);
    }

    /**
     * 测试用例11：秒杀抛出异常 - 防御性编程
     * 
     * 场景：秒杀过程中发生异常（如数据库连接失败）
     * 预期：捕获异常，返回带错误信息的失败消息
     */
    @Test
    @DisplayName("TC11-秒杀异常-返回错误信息")
    void testSeckillWithException() {
        // Arrange
        Long productId = 1L;
        String errorMessage = "库存扣减失败";
        
        when(mockProductService.seckill(productId))
                .thenThrow(new RuntimeException(errorMessage));

        // Act
        String result = productController.seckill(productId);

        // Assert
        assertTrue(result.contains("秒杀失败"), "应包含秒杀失败字样");
        assertTrue(result.contains(errorMessage), "应包含具体错误信息");
        verify(mockProductService, times(1)).seckill(productId);
    }

    /**
     * 测试用例12：保存失败 - 业务逻辑异常
     * 
     * 场景：商品信息不完整或违反业务规则
     * 预期：返回false
     */
    @Test
    @DisplayName("TC12-保存商品失败-返回false")
    void testSaveProductFailed() {
        // Arrange
        Product invalidProduct = createProduct(null, "", -10.0); // 无效数据
        
        when(mockProductService.save(invalidProduct)).thenReturn(false);

        // Act
        boolean result = productController.save(invalidProduct);

        // Assert
        assertFalse(result, "保存失败应返回false");
        verify(mockProductService, times(1)).save(invalidProduct);
    }

    /**
     * 测试用例13：空列表查询 - 边界条件
     * 
     * 场景：数据库中没有任何商品
     * 预期：返回空列表而非null
     */
    @Test
    @DisplayName("TC13-查询空列表-返回空集合")
    void testListAllEmpty() {
        // Arrange
        List<Product> emptyList = Arrays.asList();
        
        when(mockProductService.list()).thenReturn(emptyList);

        // Act
        List<Product> result = productController.listAll();

        // Assert
        assertNotNull(result, "即使没有商品也应返回空列表而非null");
        assertTrue(result.isEmpty(), "列表应为空");
        assertEquals(0, result.size(), "列表大小应为0");
        verify(mockProductService, times(1)).list();
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建测试用的商品对象
     * 
     * @param id 商品ID
     * @param productName 商品名称
     * @param price 商品价格（Double类型，会自动转换为BigDecimal）
     * @return Product对象
     */
    private Product createProduct(Long id, String productName, Double price) {
        Product product = new Product();
        product.setId(id);
        product.setProductName(productName);
        product.setPrice(price != null ? new BigDecimal(price.toString()) : null);
        product.setStock(100); // 设置默认库存
        return product;
    }
}