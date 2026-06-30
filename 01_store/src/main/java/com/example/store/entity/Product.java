package com.example.store.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 请帮我生成 Product 实体类，要求：
 * 1. 使用 Lombok 的 @Data 注解
 * 2. 映射数据库表名 "product"
 * 3. 包含字段：id (主键自增)，productName (商品名)，price (价格)，stock (库存)
 * 4. 包含审计字段：createTime 和 updateTime，并加上 MyBatis-Plus 的自动填充注解
 */
@Data
@TableName("product")
public class Product {

    /**
     * 商品主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 剩余库存量（超卖测试核心字段）
     */
    private Integer stock;

    /**
     * 创建时间（插入时自动填充）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间（插入和更新时自动填充）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
