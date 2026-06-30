package com.example.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.store.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    // 继承BaseMapper后，自动拥有CRUD方法
}