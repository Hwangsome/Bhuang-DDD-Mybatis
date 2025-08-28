# DDD 微服务架构电商系统 - 服务启动指南

## 架构概览

本系统采用基于**领域驱动设计(DDD)**的**微服务架构**，每个领域服务都是独立的Spring Boot应用，通过gRPC进行服务间通信。

### 🏗️ 服务架构图
```
┌─────────────────┐    HTTP    ┌──────────────────┐
│   API Gateway   │◄──────────►│  Orchestrator    │
│   (Port: 8080)  │            │   Service        │
└─────────────────┘            │  (Port: 8087)    │
                               └──────────────────┘
                                        │ gRPC
                    ┌───────────────────┼───────────────────┐
                    │                   │                   │
         ┌──────────▼──────────┐ ┌─────▼─────┐ ┌──────────▼──────────┐
         │   User Service      │ │  Product  │ │  Inventory Service  │
         │   (Port: 8081)     │ │  Service  │ │   (Port: 8083)     │
         │   gRPC: 9081       │ │ (8082)    │ │   gRPC: 9083       │
         └────────────────────┘ │ gRPC:9082 │ └────────────────────┘
                                └───────────┘
                    ┌───────────────────┼───────────────────┐
                    │                   │                   │
         ┌──────────▼──────────┐ ┌─────▼─────┐ ┌──────────▼──────────┐
         │   Order Service     │ │  Payment  │ │ Notification Service│
         │   (Port: 8084)     │ │  Service  │ │   (Port: 8086)     │
         │   gRPC: 9084       │ │ (8085)    │ │   gRPC: 9086       │
         └────────────────────┘ │ gRPC:9085 │ └────────────────────┘
                                └───────────┘
```

## 📋 服务端口分配

| 服务名称 | HTTP端口 | gRPC端口 | 数据库 | Redis DB |
|---------|---------|---------|--------|----------|
| User Service | 8081 | 9081 | ecommerce_user | 0 |
| Product Service | 8082 | 9082 | ecommerce_product | 1 |
| Inventory Service | 8083 | 9083 | ecommerce_inventory | 2 |
| Order Service | 8084 | 9084 | ecommerce_order | 3 |
| Payment Service | 8085 | 9085 | ecommerce_payment | 4 |
| Notification Service | 8086 | 9086 | ecommerce_notification | 5 |
| Orchestrator Service | 8087 | - | - | - |
| API Gateway | 8080 | - | - | - |

## 🚀 服务启动指南

### 前置条件
1. **Maven 3.6+** 和 **JDK 17+**
2. **MySQL 8.0+** (运行在 localhost:3306)
3. **Redis** (运行在 localhost:6379)
4. **Nacos** (运行在 localhost:8848) - 用于服务发现和配置管理
5. **RocketMQ** (运行在 localhost:9876) - 用于消息队列

### 编译依赖
首先编译共享的proto-definitions依赖：
```bash
cd proto-definitions
mvn clean install
```

### 单独启动各个服务

#### 1. 用户服务 (User Service)
```bash
cd user-service
mvn spring-boot:run -Dspring-boot.run.args="--server.port=8081"
```
- **功能**: 用户管理、认证、用户信息CRUD
- **gRPC服务**: UserServiceGrpc
- **数据库**: ecommerce_user

#### 2. 商品服务 (Product Service)
```bash
cd product-service
mvn spring-boot:run -Dspring-boot.run.args="--server.port=8082"
```
- **功能**: 商品管理、SKU管理、商品搜索
- **gRPC服务**: ProductServiceGrpc
- **数据库**: ecommerce_product

#### 3. 库存服务 (Inventory Service)
```bash
cd inventory-service
mvn spring-boot:run -Dspring-boot.run.args="--server.port=8083"
```
- **功能**: 库存管理、库存预留、库存调整
- **gRPC服务**: InventoryServiceGrpc
- **数据库**: ecommerce_inventory

#### 4. 订单服务 (Order Service)
```bash
cd order-service
mvn spring-boot:run -Dspring-boot.run.args="--server.port=8084"
```
- **功能**: 订单管理、订单状态流转
- **gRPC服务**: OrderServiceGrpc
- **数据库**: ecommerce_order

#### 5. 支付服务 (Payment Service)
```bash
cd payment-service
mvn spring-boot:run -Dspring-boot.run.args="--server.port=8085"
```
- **功能**: 支付处理、支付状态管理
- **gRPC服务**: PaymentServiceGrpc
- **数据库**: ecommerce_payment

#### 6. 通知服务 (Notification Service)
```bash
cd notification-service
mvn spring-boot:run -Dspring-boot.run.args="--server.port=8086"
```
- **功能**: 消息通知、邮件发送、短信发送
- **gRPC服务**: NotificationServiceGrpc
- **数据库**: ecommerce_notification
- **消息队列**: RocketMQ

#### 7. 业务编排服务 (Orchestrator Service)
```bash
cd orchestrator-service
mvn spring-boot:run -Dspring-boot.run.args="--server.port=8087"
```
- **功能**: 复杂业务流程编排，如下单流程
- **通信方式**: 作为gRPC客户端调用各个领域服务
- **提供**: HTTP REST API供外部调用

#### 8. API网关 (API Gateway)
```bash
cd api-gateway
mvn spring-boot:run -Dspring-boot.run.args="--server.port=8080"
```
- **功能**: 统一入口、路由转发、认证鉴权
- **路由**: 将外部HTTP请求路由到Orchestrator Service

## 🔧 服务间通信

### gRPC通信
- **内部服务间**: 使用gRPC进行高性能通信
- **Proto定义**: 统一在`proto-definitions`项目中管理
- **服务发现**: 通过Nacos进行服务注册和发现

### 调用链路示例
```
Client → API Gateway → Orchestrator Service → Domain Services (gRPC)
                                           ├── User Service
                                           ├── Product Service  
                                           ├── Inventory Service
                                           ├── Order Service
                                           ├── Payment Service
                                           └── Notification Service
```

## 📊 监控和管理

每个服务都暴露了Actuator端点用于监控：
- **健康检查**: `http://localhost:{port}/actuator/health`
- **服务信息**: `http://localhost:{port}/actuator/info`
- **Prometheus指标**: `http://localhost:{port}/actuator/prometheus`

## 🗃️ 数据库初始化

运行SQL初始化脚本：
```bash
mysql -u root -p < sql/init.sql
```

## 🚨 故障排除

### 常见问题
1. **端口占用**: 检查端口是否被其他进程占用
2. **数据库连接**: 确保MySQL服务已启动且用户名密码正确
3. **Redis连接**: 确保Redis服务已启动
4. **Nacos连接**: 确保Nacos服务已启动
5. **Proto依赖**: 确保已编译安装proto-definitions项目

### 查看服务状态
```bash
# 检查端口占用
lsof -i :8081

# 检查服务健康状态
curl http://localhost:8081/actuator/health
```

## 🎯 开发建议

1. **领域服务开发**: 每个领域服务专注于自己的业务领域，保持高内聚低耦合
2. **gRPC接口设计**: 接口定义优先，通过proto文件定义服务契约
3. **DDD实践**: 严格按照DDD层次架构组织代码
4. **事务管理**: 复杂业务事务通过Orchestrator Service协调
5. **数据库设计**: 每个服务拥有独立的数据库，避免跨服务数据依赖

---
*本系统采用现代化微服务架构，支持独立开发、部署和扩展。*