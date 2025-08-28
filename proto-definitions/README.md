# Proto Definitions

电商微服务系统的 gRPC 接口定义共享库。

## 📋 项目概述

这个项目包含了所有微服务的 gRPC 接口定义（.proto 文件）以及编译后的 Java 代码。各个微服务通过 Maven 依赖引入这个库，确保接口的一致性和版本控制。

## 🏗️ 项目结构

```
proto-definitions/
├── src/main/proto/           # Proto接口定义文件
│   ├── common.proto         # 公共消息类型
│   ├── user_service.proto   # 用户服务接口
│   ├── product_service.proto    # 商品服务接口
│   ├── inventory_service.proto  # 库存服务接口
│   ├── order_service.proto      # 订单服务接口
│   ├── payment_service.proto    # 支付服务接口
│   └── notification_service.proto  # 通知服务接口
├── target/generated-sources/ # 生成的Java代码
├── pom.xml                  # Maven配置
└── README.md               # 项目文档
```

## 🚀 使用方法

### 1. 编译Proto文件

```bash
# 编译所有proto文件，生成Java代码
mvn clean compile

# 安装到本地Maven仓库
mvn clean install

# 跳过测试安装
mvn clean install -DskipTests
```

### 2. 在微服务中引入依赖

在各个微服务的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.ecommerce</groupId>
    <artifactId>proto-definitions</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 3. 移除各服务的proto编译配置

各个微服务不再需要自己的 `protobuf-maven-plugin` 配置，直接使用编译好的类：

```java
// 直接导入使用
import com.ecommerce.proto.UserServiceGrpc;
import com.ecommerce.proto.UserProto;
import com.ecommerce.proto.ProductServiceGrpc;
import com.ecommerce.proto.ProductProto;
// ... 其他服务的proto类
```

## 📦 生成的Java包结构

编译后会生成以下Java包：

- `com.ecommerce.proto.CommonProto` - 公共消息类型
- `com.ecommerce.proto.UserServiceGrpc` + `com.ecommerce.proto.UserProto`
- `com.ecommerce.proto.ProductServiceGrpc` + `com.ecommerce.proto.ProductProto` 
- `com.ecommerce.proto.InventoryServiceGrpc` + `com.ecommerce.proto.InventoryProto`
- `com.ecommerce.proto.OrderServiceGrpc` + `com.ecommerce.proto.OrderProto`
- `com.ecommerce.proto.PaymentServiceGrpc` + `com.ecommerce.proto.PaymentProto`
- `com.ecommerce.proto.NotificationServiceGrpc` + `com.ecommerce.proto.NotificationProto`

## 🔧 版本管理

### 版本号规则
- `1.0.0-SNAPSHOT` - 开发版本
- `1.0.0` - 正式版本
- `1.1.0` - 新增接口（向后兼容）
- `2.0.0` - 破坏性变更

### 发布流程

1. **开发阶段**：使用 SNAPSHOT 版本
```bash
mvn clean deploy
```

2. **发布版本**：
```bash
# 更新版本号
mvn versions:set -DnewVersion=1.0.0

# 编译和部署
mvn clean deploy

# 提交版本标签
git tag v1.0.0
git push origin v1.0.0
```

## 📝 Proto接口设计规范

### 1. 文件命名规范
- 文件名：`{service_name}_service.proto`
- 包名：`com.ecommerce.proto`

### 2. 服务定义规范
```protobuf
// 服务名称：{ServiceName}Service
service UserService {
  // RPC方法名：动词 + 名词，如 GetUser、CreateUser
  rpc GetUser(GetUserRequest) returns (GetUserResponse);
  rpc CreateUser(CreateUserRequest) returns (CreateUserResponse);
}
```

### 3. 消息类型规范
```protobuf
// 请求消息：{Method}Request
message GetUserRequest {
  string id = 1;
}

// 响应消息：{Method}Response  
message GetUserResponse {
  User user = 1;
}
```

### 4. 版本兼容性
- 添加新字段：使用新的字段号
- 删除字段：标记为 `reserved`
- 修改字段：创建新字段，保留旧字段

## 🛠️ 开发工具

### Maven命令
```bash
# 清理并编译
mvn clean compile

# 生成源码JAR
mvn source:jar

# 生成Javadoc
mvn javadoc:jar

# 查看依赖树
mvn dependency:tree

# 检查版本
mvn versions:display-dependency-updates
```

### IDEA设置
1. 安装 Protocol Buffers 插件
2. 设置生成的源码目录为 Source Root：
   - `target/generated-sources/protobuf/java`
   - `target/generated-sources/protobuf/grpc-java`

## 🔍 故障排除

### 常见问题

1. **编译失败**
```bash
# 清理并重新编译
mvn clean compile

# 检查proto语法
protoc --proto_path=src/main/proto --java_out=target/temp src/main/proto/*.proto
```

2. **找不到protoc**
```bash
# 检查系统是否安装protoc
protoc --version

# 或确保Maven插件能下载protoc
mvn dependency:resolve-sources
```

3. **生成的类找不到**
```bash
# 确保安装到本地仓库
mvn install

# 检查生成的文件
ls -la target/generated-sources/protobuf/java/
```

## 📈 性能优化建议

1. **编译优化**：
   - 使用增量编译
   - 配置适当的内存参数

2. **依赖管理**：
   - 定期更新gRPC版本
   - 使用BOM管理版本一致性

3. **CI/CD集成**：
   - 自动化编译和发布
   - 版本标签自动化

## 🤝 贡献指南

1. 修改proto文件前先讨论设计
2. 确保向后兼容性
3. 更新版本号和文档
4. 测试编译通过
5. 提交PR并通过review

---

**维护者**: 电商微服务架构团队  
**最后更新**: 2024年