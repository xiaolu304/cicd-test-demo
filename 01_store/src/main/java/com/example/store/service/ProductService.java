package com.example.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.store.entity.Product;

/**
 * 商品业务接口
 */
public interface ProductService extends IService<Product> {
    // 继承IService后，自动拥有标准CRUD方法
    // 后续可以在这里扩展秒杀、减库存等自定义业务方法
    // 秒杀方法
    boolean seckill(Long productId);
    // 乐观锁版本的秒杀方法（和老师的命名一致）
    boolean seckillOptimisticLock(Long productId);
}