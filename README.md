# 电商微服务系统 - DDD + gRPC + Orchestrator 架构

> 基于领域驱动设计(DDD)的现代化微服务电商后台系统，采用gRPC进行服务间通信，通过Orchestrator服务编排业务逻辑并提供HTTP接口。

## 🏗️ 系统架构

### 架构设计理念

本项目体现了**矛盾驱动设计**和**奥卡姆剃刀原则**：
- **主要矛盾**：电商业务复杂性 ↔ 系统简洁性
- **解决方案**：通过DDD清晰边界划分 + gRPC协议统一 + Orchestrator编排，产生简洁而强大的分布式系统

### 分层架构

```
Client/Frontend
      ↓ HTTP/HTTPS
API Gateway (Port: 8000) - 统一入口、认证、限流、监控
      ↓ HTTP REST
Orchestrator Service (Port: 8080) - 复杂业务编排、事务协调
      ↓ gRPC调用 (业务编排)
┌─────────────────────────────────────────┐
│          Domain Services                │
│         (纯gRPC服务，简单CRUD)          │
├─────────────────────────────────────────┤
│ User Service    │ Product Service       │
│   (8001)        │    (8002)             │
├─────────────────────────────────────────┤
│ Inventory Service │ Order Service       │
│   (8003)          │    (8004)           │
├─────────────────────────────────────────┤
│ Payment Service │ Notification Service │
│   (8005)        │    (8006)             │
└─────────────────────────────────────────┘
```

## 🎯 核心特性

### DDD设计精华
- **值对象**：`UserId`、`Email`、`Phone`、`Address`等强类型，封装验证逻辑
- **聚合根**：`User`实体封装业务规则和状态管理
- **仓储模式**：领域接口与基础设施实现分离，保持领域模型纯净
- **领域驱动**：业务逻辑在领域层，应用层薄协调

### gRPC服务设计
- **类型安全**：强类型Proto消息、枚举、嵌套结构
- **版本兼容**：optional字段、reserved预留字段
- **性能优化**：连接池管理、并行调用、超时控制
- **错误处理**：统一状态码、熔断降级、重试机制

### 业务编排核心
- **8步订单流程**：用户验证→商品验证→库存预占→价格计算→创建订单→创建支付→发送通知→异常补偿
- **分布式事务**：Seata TCC模式，确保数据一致性
- **并行优化**：CompletableFuture异步调用，提升响应速度
- **补偿机制**：异常时自动回滚已执行的操作

## 🚀 技术栈

### 核心框架
- **Java 17** - 现代化Java特性
- **Spring Boot 3.x** - 微服务基础框架
- **Spring Cloud 2023.x** - 微服务治理

### 数据访问
- **MyBatis-Plus** - 数据访问层ORM
- **MySQL 8.0** - 主数据库，每服务独立DB
- **Redis Cluster** - 缓存和会话存储

### 服务通信
- **gRPC** - 服务间高性能通信
- **Protocol Buffers** - 接口定义语言
- **HTTP REST** - 对外API接口

### 服务治理
- **Nacos** - 服务注册发现 + 配置中心
- **Spring Cloud Gateway** - API网关
- **Seata** - 分布式事务解决方案

### 消息队列
- **RocketMQ** - 高性能消息队列
- **Kafka** - 大数据量消息处理（备选）

### 监控观测
- **Prometheus** - 指标收集
- **Grafana** - 可视化监控
- **Jaeger** - 分布式链路追踪
- **Micrometer** - 应用指标

### 部署运维
- **Docker** - 容器化部署
- **Docker Compose** - 本地开发环境
- **Kubernetes** - 生产环境编排

## 📁 项目结构

```
Bhuang-DDD-Mybatis/
├── proto-definitions/                 # gRPC接口定义
│   ├── common.proto                  # 公共消息类型
│   ├── user_service.proto            # 用户服务接口
│   ├── product_service.proto         # 商品服务接口
│   ├── inventory_service.proto       # 库存服务接口
│   ├── order_service.proto           # 订单服务接口
│   ├── payment_service.proto         # 支付服务接口
│   └── notification_service.proto    # 通知服务接口
├── api-gateway/                      # API网关
│   ├── src/main/java/
│   │   └── com/ecommerce/gateway/
│   │       ├── filter/              # 过滤器（认证、限流等）
│   │       ├── config/              # 网关配置
│   │       └── exception/           # 异常处理
│   └── src/main/resources/
│       └── application.yml          # 网关路由配置
├── orchestrator-service/             # 业务编排服务
│   ├── src/main/java/
│   │   └── com/ecommerce/orchestrator/
│   │       ├── controller/          # HTTP REST控制器
│   │       ├── service/             # 业务编排服务
│   │       ├── client/              # gRPC客户端
│   │       ├── dto/                 # 数据传输对象
│   │       ├── mapper/              # 对象映射
│   │       ├── config/              # 配置类
│   │       └── exception/           # 异常处理
├── user-service/                     # 用户领域服务
│   ├── src/main/java/
│   │   └── com/ecommerce/user/
│   │       ├── interfaces/          # 接口层
│   │       │   ├── grpc/           # gRPC服务实现
│   │       │   └── converter/      # Proto转换器
│   │       ├── domain/             # 领域层
│   │       │   ├── entity/         # 聚合根
│   │       │   ├── valueobject/    # 值对象
│   │       │   └── repository/     # 仓储接口
│   │       └── infrastructure/     # 基础设施层
│   │           ├── repository/     # 仓储实现
│   │           └── mapper/         # MyBatis映射
├── product-service/                  # 商品领域服务
├── inventory-service/                # 库存领域服务
├── order-service/                    # 订单领域服务
├── payment-service/                  # 支付领域服务
├── notification-service/             # 通知领域服务
├── sql/                             # 数据库脚本
│   └── init.sql                    # 初始化SQL
├── docker/                          # Docker配置
├── k8s/                            # Kubernetes配置
├── docker-compose.yml              # 本地开发环境
└── README.md                       # 项目文档
```

## 🛠️ 快速开始

### 环境要求
- **JDK 17+**
- **Maven 3.8+**
- **Docker & Docker Compose**
- **MySQL 8.0+**
- **Redis 7.0+**

### 本地开发环境启动

1. **克隆项目**
```bash
git clone <repository-url>
cd Bhuang-DDD-Mybatis
```

2. **启动基础设施**
```bash
# 启动MySQL、Redis、Nacos、RocketMQ等基础服务
docker-compose up -d mysql redis nacos rocketmq-nameserver rocketmq-broker
```

3. **等待服务就绪**
```bash
# 检查服务健康状态
docker-compose ps
```

4. **启动微服务**
```bash
# 按顺序启动服务
docker-compose up -d user-service product-service inventory-service order-service payment-service notification-service

# 启动编排服务
docker-compose up -d orchestrator-service

# 启动API网关
docker-compose up -d api-gateway
```

5. **启动监控服务**
```bash
docker-compose up -d prometheus grafana jaeger
```

### 服务访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| API Gateway | http://localhost:8000 | 统一API入口 |
| Orchestrator | http://localhost:8080 | 业务编排服务 |
| User Service | localhost:8001 | 用户服务(gRPC) |
| Product Service | localhost:8002 | 商品服务(gRPC) |
| Inventory Service | localhost:8003 | 库存服务(gRPC) |
| Order Service | localhost:8004 | 订单服务(gRPC) |
| Payment Service | localhost:8005 | 支付服务(gRPC) |
| Notification Service | localhost:8006 | 通知服务(gRPC) |
| Nacos Console | http://localhost:8848/nacos | 服务注册中心 |
| Grafana | http://localhost:3000 | 监控面板 |
| Prometheus | http://localhost:9090 | 指标收集 |
| Jaeger | http://localhost:16686 | 链路追踪 |

### API测试示例

```bash
# 用户注册
curl -X POST http://localhost:8000/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "phone": "13800138000",
    "firstName": "John",
    "lastName": "Doe"
  }'

# 创建订单
curl -X POST http://localhost:8000/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt_token>" \
  -d '{
    "userId": "user-id",
    "items": [
      {
        "skuId": "sku-001",
        "quantity": 2
      }
    ],
    "shippingAddress": {
      "country": "中国",
      "province": "北京",
      "city": "北京市",
      "street": "朝阳区xxx街道",
      "contactName": "张三",
      "contactPhone": "13800138000"
    }
  }'
```

## 🔧 开发指南

### Domain Service开发规范
- **单一职责**：每个服务只管理一个聚合根
- **简单CRUD**：提供基础的增删改查操作
- **gRPC接口**：只暴露gRPC服务，不包含HTTP
- **数据验证**：在领域层进行业务规则验证

### Orchestrator Service开发规范
- **业务编排**：协调多个Domain Service完成复杂业务
- **HTTP接口**：对外提供REST API
- **事务管理**：使用@GlobalTransactional注解
- **异常处理**：实现补偿逻辑和回滚机制

### 代码质量规范
- **单元测试**：核心业务逻辑覆盖率>80%
- **集成测试**：使用TestContainers测试
- **代码规范**：遵循阿里巴巴Java开发手册
- **文档更新**：接口变更及时更新文档

## 🚢 生产部署

### Kubernetes部署
```bash
# 部署基础设施
kubectl apply -f k8s/infrastructure/

# 部署微服务
kubectl apply -f k8s/services/

# 部署监控
kubectl apply -f k8s/monitoring/
```

### 配置管理
- 使用Nacos Config统一管理配置
- 敏感信息使用Kubernetes Secret
- 环境隔离：dev、test、prod

### 监控告警
- **业务指标**：订单量、支付成功率、库存告警
- **技术指标**：CPU、内存、响应时间、错误率
- **告警规则**：Prometheus + AlertManager

## 📊 性能指标

### 目标性能
- **API响应时间**：P99 < 500ms
- **服务可用性**：99.95%
- **并发处理**：支持1000+ TPS
- **数据一致性**：最终一致性保证

### 优化策略
- **缓存策略**：多级缓存，热点数据预加载
- **数据库优化**：读写分离、分库分表
- **异步处理**：消息队列解耦，提升响应速度
- **资源隔离**：服务间资源隔离，防止雪崩

## 🤝 贡献指南

1. Fork项目到个人仓库
2. 创建特性分支：`git checkout -b feature/amazing-feature`
3. 提交变更：`git commit -m 'Add amazing feature'`
4. 推送分支：`git push origin feature/amazing-feature`
5. 创建Pull Request

### 代码提交规范
```
<type>(<scope>): <description>

[optional body]

[optional footer(s)]
```

类型：feat、fix、docs、style、refactor、test、chore

## 📝 更新日志

### v1.0.0 (2024-XX-XX)
- ✨ 实现DDD+gRPC+Orchestrator微服务架构
- 🚀 完成用户、商品、库存、订单、支付、通知六大领域服务
- 🔧 集成Nacos服务治理和配置管理
- 📊 集成Prometheus+Grafana监控体系
- 🐳 提供Docker Compose本地开发环境

## 📄 许可证

本项目采用 [MIT许可证](LICENSE)

## 👥 团队

- **架构师**：负责系统设计和技术选型
- **后端开发**：微服务开发和接口实现
- **运维工程师**：部署运维和监控告警
- **测试工程师**：质量保证和性能测试

## 🙏 致谢

感谢以下开源项目和技术社区的支持：
- Spring Boot & Spring Cloud
- gRPC & Protocol Buffers  
- Nacos & Seata
- Docker & Kubernetes
- Prometheus & Grafana

---

> **设计理念**：通过DDD清晰的领域边界划分，解决了电商业务复杂性与系统简洁性的矛盾，体现了奥卡姆剃刀原则在大型分布式系统设计中的应用。