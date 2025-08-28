# Database Setup Guide

This guide explains how to set up MyBatis-Plus for database persistence in the E-commerce microservices system.

## Overview

The following services have been configured with MyBatis-Plus for database persistence:
- Payment Service (port: 8085, gRPC: 9085)
- Order Service (port: 8084, gRPC: 9084)
- Notification Service (port: 8086, gRPC: 9086)
- Inventory Service (port: 8083, gRPC: 9083)

## Database Schema

### 1. Database Creation

Execute the following SQL scripts to create the databases:

```bash
# Execute each SQL script
mysql -u root -p < sql/payment_service.sql
mysql -u root -p < sql/order_service.sql
mysql -u root -p < sql/notification_service.sql
mysql -u root -p < sql/inventory_service.sql
```

### 2. Database Structures

#### Payment Service Database (`ecommerce_payment`)
- **payment** table: Stores payment records
- **refund** table: Stores refund records

#### Order Service Database (`ecommerce_order`)
- **orders** table: Stores order information
- **order_item** table: Stores order item details

#### Notification Service Database (`ecommerce_notification`)
- **notification** table: Stores notification records

#### Inventory Service Database (`ecommerce_inventory`)
- **inventory** table: Stores inventory information
- **inventory_operation** table: Stores inventory operation logs

## MyBatis-Plus Configuration

### 1. Configuration Classes

Each service includes a `MyBatisPlusConfig` class:
- Pagination support with MySQL dialect
- Mapper scanning for repository interfaces

### 2. Entity Mapping

Each service has:
- **PO (Persistence Object)** classes with MyBatis-Plus annotations
- **Mapper** interfaces extending `BaseMapper<T>` with custom queries
- **DataMapper** implementations for domain/persistence object conversion
- **Repository** implementations following DDD patterns

### 3. Key Features Implemented

- **Auto-generated IDs**: Using `@TableId(type = IdType.AUTO)`
- **Camel Case Mapping**: Database snake_case to Java camelCase
- **Custom Queries**: Additional finder methods with `@Select` annotations
- **Pagination Support**: Built-in pagination with `PaginationInnerInterceptor`

## Application Configuration

### Database Connection Settings

Each service's `application.yml` includes:
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/ecommerce_[service]?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: root

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: auto
```

## Testing

### Unit Tests

Example test classes have been created:
- `PaymentRepositoryTest`: Tests payment persistence operations

### Integration Testing

To test the database integration:

1. Start MySQL server
2. Execute the SQL scripts to create databases
3. Run the unit tests: `mvn test`

## Project Structure

Each service follows this structure:
```
src/main/java/com/ecommerce/[service]/
├── infrastructure/
│   ├── config/
│   │   └── MyBatisPlusConfig.java
│   ├── entity/
│   │   └── [Entity]PO.java
│   ├── mapper/
│   │   ├── [Entity]Mapper.java
│   │   └── [Entity]DataMapperImpl.java
│   └── repository/
│       └── [Entity]RepositoryImpl.java
```

## Dependencies

The following dependencies are included in each service's `pom.xml`:
- `mybatis-plus-boot-starter`: MyBatis-Plus Spring Boot starter
- `mysql-connector-java`: MySQL JDBC driver
- `druid-spring-boot-starter`: Alibaba Druid connection pool

## Sample Data

Each database includes sample data for testing:
- Payment records with different statuses
- Order records with order items
- Notification records with different types and channels
- Inventory records with different quantities

## Running the Services

1. Start MySQL server
2. Execute database scripts
3. Build the project: `mvn clean compile`
4. Start each service: `mvn spring-boot:run`

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check MySQL server is running
   - Verify connection parameters in `application.yml`
   - Ensure databases are created

2. **Mapper Not Found**
   - Verify `@MapperScan` annotation in configuration
   - Check mapper interface location

3. **Entity Mapping Issues**
   - Verify `@TableName` annotation matches table name
   - Check field mappings with `@TableField`

### Logs

Enable debug logging in `application.yml`:
```yaml
logging:
  level:
    com.ecommerce.[service]: DEBUG
```

This will show SQL statements and parameter bindings for debugging.