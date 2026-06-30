package com.example.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.store.entity.Product;
import com.example.store.mapper.ProductMapper;
import com.example.store.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean seckill(Long productId) {
        Product product = this.getById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStock() <= 0) {
            throw new RuntimeException("库存不足，秒杀失败");
        }

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getId, productId)
               .eq(Product::getStock, product.getStock());

        product.setStock(product.getStock() - 1);
        int rows = baseMapper.update(product, wrapper);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean seckillOptimisticLock(Long productId) {
        // 乐观锁版本的秒杀逻辑，和上面的 seckill 完全一致
        Product product = this.getById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStock() <= 0) {
            throw new RuntimeException("库存不足，秒杀失败");
        }

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getId, productId)
               .eq(Product::getStock, product.getStock());

        product.setStock(product.getStock() - 1);
        int rows = baseMapper.update(product, wrapper);
        return rows > 0;
    }
}