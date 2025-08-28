package com.ecommerce.orchestrator.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * gRPC客户端配置类
 * 职责：配置与其他微服务的gRPC连接
 */
@Configuration
@ConfigurationProperties(prefix = "grpc.client")
public class GrpcClientConfig {
    
    private String userServiceHost = "localhost";
    private int userServicePort = 9091;
    
    private String productServiceHost = "localhost";
    private int productServicePort = 9092;
    
    private String inventoryServiceHost = "localhost";
    private int inventoryServicePort = 9093;
    
    private String orderServiceHost = "localhost";
    private int orderServicePort = 9094;
    
    private String paymentServiceHost = "localhost";
    private int paymentServicePort = 9095;
    
    private String notificationServiceHost = "localhost";
    private int notificationServicePort = 9096;
    
    /**
     * 用户服务gRPC通道
     */
    @Bean
    public ManagedChannel userServiceChannel() {
        return ManagedChannelBuilder.forAddress(userServiceHost, userServicePort)
                .usePlaintext() // 在生产环境中应该使用TLS
                .keepAliveTime(30, java.util.concurrent.TimeUnit.SECONDS)
                .keepAliveTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                .keepAliveWithoutCalls(true)
                .maxInboundMessageSize(1024 * 1024) // 1MB
                .build();
    }
    
    /**
     * 商品服务gRPC通道
     */
    @Bean
    public ManagedChannel productServiceChannel() {
        return ManagedChannelBuilder.forAddress(productServiceHost, productServicePort)
                .usePlaintext() // 在生产环境中应该使用TLS
                .keepAliveTime(30, java.util.concurrent.TimeUnit.SECONDS)
                .keepAliveTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                .keepAliveWithoutCalls(true)
                .maxInboundMessageSize(1024 * 1024) // 1MB
                .build();
    }
    
    /**
     * 库存服务gRPC通道
     */
    @Bean
    public ManagedChannel inventoryServiceChannel() {
        return ManagedChannelBuilder.forAddress(inventoryServiceHost, inventoryServicePort)
                .usePlaintext() // 在生产环境中应该使用TLS
                .keepAliveTime(30, java.util.concurrent.TimeUnit.SECONDS)
                .keepAliveTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                .keepAliveWithoutCalls(true)
                .maxInboundMessageSize(1024 * 1024) // 1MB
                .build();
    }
    
    /**
     * 订单服务gRPC通道
     */
    @Bean
    public ManagedChannel orderServiceChannel() {
        return ManagedChannelBuilder.forAddress(orderServiceHost, orderServicePort)
                .usePlaintext() // 在生产环境中应该使用TLS
                .keepAliveTime(30, java.util.concurrent.TimeUnit.SECONDS)
                .keepAliveTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                .keepAliveWithoutCalls(true)
                .maxInboundMessageSize(1024 * 1024) // 1MB
                .build();
    }
    
    /**
     * 支付服务gRPC通道
     */
    @Bean
    public ManagedChannel paymentServiceChannel() {
        return ManagedChannelBuilder.forAddress(paymentServiceHost, paymentServicePort)
                .usePlaintext() // 在生产环境中应该使用TLS
                .keepAliveTime(30, java.util.concurrent.TimeUnit.SECONDS)
                .keepAliveTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                .keepAliveWithoutCalls(true)
                .maxInboundMessageSize(1024 * 1024) // 1MB
                .build();
    }
    
    /**
     * 通知服务gRPC通道
     */
    @Bean
    public ManagedChannel notificationServiceChannel() {
        return ManagedChannelBuilder.forAddress(notificationServiceHost, notificationServicePort)
                .usePlaintext() // 在生产环境中应该使用TLS
                .keepAliveTime(30, java.util.concurrent.TimeUnit.SECONDS)
                .keepAliveTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                .keepAliveWithoutCalls(true)
                .maxInboundMessageSize(1024 * 1024) // 1MB
                .build();
    }
    
    // Getters and Setters for configuration properties
    
    public String getUserServiceHost() {
        return userServiceHost;
    }
    
    public void setUserServiceHost(String userServiceHost) {
        this.userServiceHost = userServiceHost;
    }
    
    public int getUserServicePort() {
        return userServicePort;
    }
    
    public void setUserServicePort(int userServicePort) {
        this.userServicePort = userServicePort;
    }
    
    public String getProductServiceHost() {
        return productServiceHost;
    }
    
    public void setProductServiceHost(String productServiceHost) {
        this.productServiceHost = productServiceHost;
    }
    
    public int getProductServicePort() {
        return productServicePort;
    }
    
    public void setProductServicePort(int productServicePort) {
        this.productServicePort = productServicePort;
    }
    
    public String getInventoryServiceHost() {
        return inventoryServiceHost;
    }
    
    public void setInventoryServiceHost(String inventoryServiceHost) {
        this.inventoryServiceHost = inventoryServiceHost;
    }
    
    public int getInventoryServicePort() {
        return inventoryServicePort;
    }
    
    public void setInventoryServicePort(int inventoryServicePort) {
        this.inventoryServicePort = inventoryServicePort;
    }
    
    public String getOrderServiceHost() {
        return orderServiceHost;
    }
    
    public void setOrderServiceHost(String orderServiceHost) {
        this.orderServiceHost = orderServiceHost;
    }
    
    public int getOrderServicePort() {
        return orderServicePort;
    }
    
    public void setOrderServicePort(int orderServicePort) {
        this.orderServicePort = orderServicePort;
    }
    
    public String getPaymentServiceHost() {
        return paymentServiceHost;
    }
    
    public void setPaymentServiceHost(String paymentServiceHost) {
        this.paymentServiceHost = paymentServiceHost;
    }
    
    public int getPaymentServicePort() {
        return paymentServicePort;
    }
    
    public void setPaymentServicePort(int paymentServicePort) {
        this.paymentServicePort = paymentServicePort;
    }
    
    public String getNotificationServiceHost() {
        return notificationServiceHost;
    }
    
    public void setNotificationServiceHost(String notificationServiceHost) {
        this.notificationServiceHost = notificationServiceHost;
    }
    
    public int getNotificationServicePort() {
        return notificationServicePort;
    }
    
    public void setNotificationServicePort(int notificationServicePort) {
        this.notificationServicePort = notificationServicePort;
    }
}