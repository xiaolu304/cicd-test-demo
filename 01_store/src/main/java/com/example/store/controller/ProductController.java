package com.example.store.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.store.entity.Product;
import com.example.store.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "商品管理接口", description = "商品的增删改查、分页、详情查询")
public class ProductController {

    private final ProductService productService;

    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "查询所有商品", description = "返回数据库中所有商品列表")
    @GetMapping
    public List<Product> listAll() {
        return productService.list();
    }

    @Operation(summary = "分页查询商品", description = "支持页码和每页条数参数")
    @GetMapping("/page")
    public Page<Product> listPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return productService.page(new Page<>(page, size));
    }

    @Operation(summary = "根据ID查询单个商品", description = "传入商品ID，返回详情")
    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @Operation(summary = "新增商品", description = "传入JSON格式商品信息，添加到数据库")
    @PostMapping
    public boolean save(@RequestBody Product product) {
        return productService.save(product);
    }

    @Operation(summary = "修改商品", description = "根据ID更新商品信息")
    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return productService.updateById(product);
    }

    @Operation(summary = "删除商品", description = "根据ID删除指定商品")
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return productService.removeById(id);
    }

    @GetMapping("/{id}/seckill")
public String seckill(@PathVariable Long id) {
    try {
        boolean success = productService.seckill(id);
        return success ? "秒杀成功" : "秒杀失败";
    } catch (Exception e) {
        return "秒杀失败：" + e.getMessage();
    }
}

}