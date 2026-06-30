# Spring Boot Store Application - CI/CD 自动化部署项目

##  项目信息

- **项目名称**: Spring Boot Store Application
- **作者**: 陆梦琳
- **日期**: 2026-06-26
- **技术栈**: Spring Boot 3.2.3 + MyBatis-Plus + MySQL 8.0 + JDK 17

---

## 🎯 项目简介

本项目是一个电商商品管理系统，实现了以下核心功能：

### 1. 后端功能模块

####  商品管理
- 商品的增删改查（CRUD）
- 分页查询支持
- RESTful API 设计

####  折扣计算系统（策略模式）
- **多种商品类型支持**：
  - 电子产品（Electronics）
  - 服装（Clothing）
  - 图书（Book）
  - 生鲜（Fresh）
  
- **用户等级支持**：
  - 新用户优惠
  - 普通用户
  - 黄金VIP（9折）
  - 钻石VIP（额外减10元）
  
- **特殊规则**：
  - 节假日满300打9折
  - 生鲜商品不参与节假日折扣
  - 负价格保护机制

#### 🌍 海外配送系统（策略模式）
- 基于用户类型和仓库位置的运费计算
- 支持国内仓和海外仓
- 灵活的配送策略扩展

### 2. 单元测试

#### ✅ 测试覆盖
- **DiscountController 测试**：7个测试用例
  - TC01: 电子产品新用户正常折扣计算
  - TC02: 服装节假日VIP折扣叠加
  - TC03: 无效商品类型错误处理
  - TC04: 零价格商品边界值处理
  - TC05: 钻石VIP节假日额外减免
  - TC06: 生鲜商品节假日例外规则
  - TC07: 负价格保护机制
  
- **ProductController 测试**：13个测试用例
  - 商品查询、新增、修改、删除
  - 分页查询验证
  - 参数校验测试
  - 异常处理测试

#### 🧪 测试框架
- JUnit 5
- Mockito（Mock对象）
- 3A原则（Arrange-Act-Assert）

#### 📊 代码覆盖率
- **JaCoCo 覆盖率报告**：96%+
- **Surefire HTML 报告**：详细测试结果

### 3. DevOps 自动化（CI/CD）

#### 🚀 GitHub Actions 流水线
- **触发条件**：仅在 push 到 `main` 分支时触发
- **阶段1：构建和测试**
  - 自动搭建 JDK 17 环境
  - 自动启动 MySQL 8.0 服务
  - 执行 Maven 编译和单元测试
  - 生成测试报告和覆盖率报告
  
- **阶段2：Docker 镜像构建**
  - 多阶段构建优化镜像大小
  - 使用 Buildx 加速构建
  - 缓存依赖提升效率
  
- **阶段3：部署（可选）**
  - SSH 远程部署到生产服务器
  - Docker 容器运行和管理

#### 🐳 Docker 容器化
- **多阶段构建**：
  - 阶段1：使用 Maven 镜像编译打包
  - 阶段2：使用 JDK 17 镜像运行应用
  
- **优化措施**：
  - 减小镜像体积
  - 分层缓存加速构建
  - 环境变量配置

---

## 📁 项目结构
01store/
├── .github/workflows/
│ └── ci-cd.yml # GitHub Actions CI/CD 配置（247行，含中文注释）
├── 01store/
│ ├── src/main/java/com/example/store/
│ │ ├── config/ # 配置类
│ │ │ ├── MyMetaObjectHandler.java # MyBatis-Plus 自动填充
│ │ │ └── OpenApiConfig.java # Swagger/OpenAPI 配置
│ │ ├── controller/ # HTTP 接口层
│ │ │ ├── DiscountController.java # 折扣计算接口
│ │ │ ├── ProductController.java # 商品管理接口
│ │ │ ├── OverseasShippingController.java # 海外配送接口
│ │ │ └── ShippingController.java # 配送接口
│ │ ├── entity/ # 实体类
│ │ │ └── Product.java # 商品实体
│ │ ├── mapper/ # 数据访问层
│ │ │ └── ProductMapper.java # 商品 Mapper
│ │ ├── service/ # 业务逻辑层
│ │ │ ├── ProductService.java # 商品服务接口
│ │ │ └── impl/ProductServiceImpl.java # 商品服务实现
│ │ ├── strategy/ # 折扣策略模式
│ │ │ ├── IDiscountStrategy.java # 策略接口
│ │ │ ├── DiscountStrategyFactory.java # 策略工厂
│ │ │ └── impl/ # 具体策略实现
│ │ │ ├── ElectronicsDiscountStrategy.java
│ │ │ ├── ClothingDiscountStrategy.java
│ │ │ ├── BookDiscountStrategy.java
│ │ │ └── FreshDiscountStrategy.java
│ │ ├── shipping/ # 配送策略模式
│ │ │ ├── IShippingStrategy.java # 配送策略接口
│ │ │ ├── ShippingStrategyFactory.java # 配送策略工厂
│ │ │ └── impl/ # 具体配送策略
│ │ │ ├── Double11NewUserStrategy.java
│ │ │ ├── Double11NormalUserStrategy.java
│ │ │ ├── Double11GoldVipStrategy.java
│ │ │ ├── Double11DiamondVipStrategy.java
│ │ │ ├── NormalNewUserStrategy.java
│ │ │ ├── NormalNormalUserStrategy.java
│ │ │ ├── NormalGoldVipStrategy.java
│ │ │ ├── NormalDiamondVipStrategy.java
│ │ │ └── OverseasWarehouseStrategy.java
│ │ └── StoreApplication.java # Spring Boot 主启动类
│ ├── src/test/java/com/example/store/controller/
│ │ ├── DiscountControllerTest.java # 折扣控制器测试（7个用例）
│ │ └── ProductControllerTest.java # 商品控制器测试（13个用例）
│ ├── src/main/resources/
│ │ └── application.yml # Spring Boot 配置文件
│ ├── pom.xml # Maven 依赖配置
│ └── target/site/ # 测试报告输出目录
│ ├── surefire-report.html # Surefire 测试报告
│ └── jacoco/ # JaCoCo 覆盖率报告
├── Dockerfile # Docker 镜像构建文件（多阶段构建）
└── README.md # 本文档