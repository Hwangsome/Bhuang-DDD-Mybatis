#!/bin/bash

# 批量更新微服务pom.xml，移除protobuf编译配置，改为依赖proto-definitions

SERVICES=("inventory-service" "order-service" "payment-service" "notification-service" "user-service" "orchestrator-service")

for SERVICE in "${SERVICES[@]}"
do
    POM_FILE="/Users/bhuang/code/Bhuang-DDD-Mybatis/$SERVICE/pom.xml"
    
    if [ -f "$POM_FILE" ]; then
        echo "更新 $SERVICE/pom.xml..."
        
        # 备份原文件
        cp "$POM_FILE" "$POM_FILE.backup"
        
        # 使用sed进行替换
        # 1. 替换gRPC依赖部分
        sed -i.tmp '/<dependency>/,/<\/dependency>/{
            /<groupId>io\.grpc<\/groupId>/,/<\/dependency>/{
                /<groupId>io\.grpc<\/groupId>/d
                /<artifactId>grpc-stub<\/artifactId>/d
                /<version>${grpc\.version}<\/version>/d
                /<\/dependency>/d
            }
        }' "$POM_FILE"
        
        sed -i.tmp '/<dependency>/,/<\/dependency>/{
            /<groupId>io\.grpc<\/groupId>/,/<\/dependency>/{
                /<groupId>io\.grpc<\/groupId>/d
                /<artifactId>grpc-protobuf<\/artifactId>/d
                /<version>${grpc\.version}<\/version>/d
                /<\/dependency>/d
            }
        }' "$POM_FILE"
        
        sed -i.tmp '/<dependency>/,/<\/dependency>/{
            /<groupId>com\.google\.protobuf<\/groupId>/,/<\/dependency>/{
                /<groupId>com\.google\.protobuf<\/groupId>/d
                /<artifactId>protobuf-java<\/artifactId>/d
                /<version>${protobuf\.version}<\/version>/d
                /<\/dependency>/d
            }
        }' "$POM_FILE"
        
        # 2. 在grpc-server-spring-boot-starter依赖后添加proto-definitions依赖
        sed -i.tmp '/<artifactId>grpc-server-spring-boot-starter<\/artifactId>/a\
        </dependency>\
        \
        <!-- Proto Definitions - 统一的gRPC接口定义 -->\
        <dependency>\
            <groupId>com.ecommerce</groupId>\
            <artifactId>proto-definitions</artifactId>\
            <version>1.0.0-SNAPSHOT</version>
' "$POM_FILE"
        
        # 清理临时文件
        rm -f "$POM_FILE.tmp"
        
        echo "$SERVICE pom.xml 更新完成"
    else
        echo "警告: $POM_FILE 不存在"
    fi
done

echo "所有服务pom.xml更新完成！"
echo ""
echo "请手动检查以下内容："
echo "1. 移除protobuf-maven-plugin插件配置"
echo "2. 移除os-maven-plugin配置" 
echo "3. 确保proto-definitions依赖已正确添加"
echo ""
echo "下一步:"
echo "1. cd proto-definitions && mvn clean install"
echo "2. 测试各个服务的编译: mvn clean compile"