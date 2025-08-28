package com.ecommerce.product.infrastructure.repository;

import com.ecommerce.product.domain.entity.Product;
import com.ecommerce.product.domain.entity.ProductStatus;
import com.ecommerce.product.domain.entity.Sku;
import com.ecommerce.product.domain.entity.SkuStatus;
import com.ecommerce.product.domain.repository.ProductRepository;
import com.ecommerce.product.domain.valueobject.*;
import com.ecommerce.product.infrastructure.mapper.ProductDataMapper;
import com.ecommerce.product.infrastructure.mapper.SkuDataMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品仓储实现 - 基础设施层
 * 职责：实现领域仓储接口，处理数据持久化和领域对象转换
 */
@Repository
public class ProductRepositoryImpl implements ProductRepository {
    
    private final ProductDataMapper productMapper;
    private final SkuDataMapper skuMapper;
    private final ObjectMapper objectMapper;
    
    public ProductRepositoryImpl(ProductDataMapper productMapper, SkuDataMapper skuMapper, ObjectMapper objectMapper) {
        this.productMapper = productMapper;
        this.skuMapper = skuMapper;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public Product save(Product product) {
        ProductDataMapper.ProductDataObject dataObject = convertToDataObject(product);
        
        if (productMapper.selectById(dataObject.getId()) == null) {
            productMapper.insert(dataObject);
        } else {
            productMapper.updateById(dataObject);
        }
        
        // 保存SKU信息
        saveSkus(product.getSkus());
        
        return product;
    }
    
    @Override
    public Optional<Product> findById(ProductId productId) {
        ProductDataMapper.ProductDataObject dataObject = productMapper.selectById(productId.getValue());
        if (dataObject == null || "DELETED".equals(dataObject.getStatus())) {
            return Optional.empty();
        }
        
        return Optional.of(convertToDomainObject(dataObject, Collections.emptyList()));
    }
    
    @Override
    public Optional<Product> findByIdWithSkus(ProductId productId) {
        ProductDataMapper.ProductDataObject dataObject = productMapper.selectById(productId.getValue());
        if (dataObject == null || "DELETED".equals(dataObject.getStatus())) {
            return Optional.empty();
        }
        
        List<ProductDataMapper.SkuDataObject> skuDataObjects = skuMapper.findByProductId(productId.getValue());
        List<Sku> skus = skuDataObjects.stream()
                .map(this::convertSkuToDomainObject)
                .collect(Collectors.toList());
        
        return Optional.of(convertToDomainObject(dataObject, skus));
    }
    
    @Override
    public boolean existsById(ProductId productId) {
        ProductDataMapper.ProductDataObject dataObject = productMapper.selectById(productId.getValue());
        return dataObject != null && !"DELETED".equals(dataObject.getStatus());
    }
    
    @Override
    public List<Product> findByName(String name) {
        return productMapper.selectList(
                new QueryWrapper<ProductDataMapper.ProductDataObject>()
                        .eq("name", name)
                        .ne("status", "DELETED")
        ).stream()
                .map(dataObject -> convertToDomainObject(dataObject, Collections.emptyList()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByNameContaining(String keyword) {
        return productMapper.findByKeyword(keyword).stream()
                .map(dataObject -> convertToDomainObject(dataObject, Collections.emptyList()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByCategoryId(CategoryId categoryId) {
        return productMapper.selectList(
                new QueryWrapper<ProductDataMapper.ProductDataObject>()
                        .eq("category_id", categoryId.getValue())
                        .ne("status", "DELETED")
                        .orderByDesc("updated_at")
        ).stream()
                .map(dataObject -> convertToDomainObject(dataObject, Collections.emptyList()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByCategoryIdStartingWith(String categoryPath) {
        return productMapper.findByCategoryPrefix(categoryPath).stream()
                .map(dataObject -> convertToDomainObject(dataObject, Collections.emptyList()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByBrand(String brand) {
        return productMapper.selectList(
                new QueryWrapper<ProductDataMapper.ProductDataObject>()
                        .eq("brand", brand)
                        .ne("status", "DELETED")
                        .orderByDesc("updated_at")
        ).stream()
                .map(dataObject -> convertToDomainObject(dataObject, Collections.emptyList()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByStatus(ProductStatus status) {
        return productMapper.selectList(
                new QueryWrapper<ProductDataMapper.ProductDataObject>()
                        .eq("status", status.name())
                        .orderByDesc("updated_at")
        ).stream()
                .map(dataObject -> convertToDomainObject(dataObject, Collections.emptyList()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByCriteria(String keyword, CategoryId categoryId, String brand, ProductStatus status) {
        return productMapper.findByCriteria(
                keyword,
                categoryId != null ? categoryId.getValue() : null,
                brand,
                status != null ? status.name() : null,
                0,
                Integer.MAX_VALUE
        ).stream()
                .map(dataObject -> convertToDomainObject(dataObject, Collections.emptyList()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findAll(int page, int size) {
        int offset = page * size;
        return productMapper.selectList(
                new QueryWrapper<ProductDataMapper.ProductDataObject>()
                        .ne("status", "DELETED")
                        .orderByDesc("updated_at")
                        .last("LIMIT " + offset + ", " + size)
        ).stream()
                .map(dataObject -> convertToDomainObject(dataObject, Collections.emptyList()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByCriteria(String keyword, CategoryId categoryId, String brand, 
                                      ProductStatus status, int page, int size) {
        int offset = page * size;
        return productMapper.findByCriteria(
                keyword,
                categoryId != null ? categoryId.getValue() : null,
                brand,
                status != null ? status.name() : null,
                offset,
                size
        ).stream()
                .map(dataObject -> convertToDomainObject(dataObject, Collections.emptyList()))
                .collect(Collectors.toList());
    }
    
    @Override
    public long count() {
        return productMapper.selectCount(
                new QueryWrapper<ProductDataMapper.ProductDataObject>()
                        .ne("status", "DELETED")
        );
    }
    
    @Override
    public long countByCriteria(String keyword, CategoryId categoryId, String brand, ProductStatus status) {
        return productMapper.countByCriteria(
                keyword,
                categoryId != null ? categoryId.getValue() : null,
                brand,
                status != null ? status.name() : null
        );
    }
    
    @Override
    public long countByCategoryId(CategoryId categoryId) {
        return productMapper.selectCount(
                new QueryWrapper<ProductDataMapper.ProductDataObject>()
                        .eq("category_id", categoryId.getValue())
                        .ne("status", "DELETED")
        );
    }
    
    @Override
    public long countByBrand(String brand) {
        return productMapper.selectCount(
                new QueryWrapper<ProductDataMapper.ProductDataObject>()
                        .eq("brand", brand)
                        .ne("status", "DELETED")
        );
    }
    
    @Override
    public long countByStatus(ProductStatus status) {
        return productMapper.selectCount(
                new QueryWrapper<ProductDataMapper.ProductDataObject>()
                        .eq("status", status.name())
        );
    }
    
    @Override
    public List<Product> findPopularProducts(int limit) {
        // 简单实现，实际应该根据销量等数据排序
        return productMapper.selectList(
                new QueryWrapper<ProductDataMapper.ProductDataObject>()
                        .eq("status", "ACTIVE")
                        .orderByDesc("updated_at")
                        .last("LIMIT " + limit)
        ).stream()
                .map(dataObject -> convertToDomainObject(dataObject, Collections.emptyList()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findRecommendedProducts(CategoryId categoryId, int limit) {
        return productMapper.selectList(
                new QueryWrapper<ProductDataMapper.ProductDataObject>()
                        .eq("category_id", categoryId.getValue())
                        .eq("status", "ACTIVE")
                        .orderByDesc("updated_at")
                        .last("LIMIT " + limit)
        ).stream()
                .map(dataObject -> convertToDomainObject(dataObject, Collections.emptyList()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findLatestProducts(int limit) {
        return productMapper.findLatestProducts(limit).stream()
                .map(dataObject -> convertToDomainObject(dataObject, Collections.emptyList()))
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(ProductId productId) {
        productMapper.deleteById(productId.getValue());
        skuMapper.deleteByProductId(productId.getValue());
    }
    
    @Override
    public List<Product> saveAll(List<Product> products) {
        return products.stream()
                .map(this::save)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByIds(List<ProductId> productIds) {
        List<String> ids = productIds.stream()
                .map(ProductId::getValue)
                .collect(Collectors.toList());
        
        return productMapper.selectBatchIds(ids).stream()
                .filter(dataObject -> !"DELETED".equals(dataObject.getStatus()))
                .map(dataObject -> convertToDomainObject(dataObject, Collections.emptyList()))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByNameAndIdNot(String name, ProductId excludeId) {
        return productMapper.countByNameAndIdNot(name, excludeId.getValue()) > 0;
    }
    
    @Override
    public long getProductCountByCategory(CategoryId categoryId) {
        return countByCategoryId(categoryId);
    }
    
    @Override
    public List<Product> findProductsNeedingUpdate() {
        // 简单实现，可以根据业务需求扩展
        return findByStatus(ProductStatus.DRAFT);
    }
    
    @Override
    public List<Product> searchProducts(String keyword, CategoryId categoryId, ProductStatus status, int offset, int pageSize) {
        return productMapper.findByCriteria(
                keyword,
                categoryId != null ? categoryId.getValue() : null,
                null, // brand参数
                status != null ? status.name() : null,
                offset,
                pageSize
        ).stream()
                .map(dataObject -> convertToDomainObject(dataObject, Collections.emptyList()))
                .collect(Collectors.toList());
    }
    
    /**
     * 领域对象转数据对象
     */
    private ProductDataMapper.ProductDataObject convertToDataObject(Product product) {
        return new ProductDataMapper.ProductDataObject(
                product.getId().getValue(),
                product.getName(),
                product.getDescription(),
                product.getCategoryId().getValue(),
                product.getBrand(),
                product.getStatus().name(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
    
    /**
     * 数据对象转领域对象
     */
    private Product convertToDomainObject(ProductDataMapper.ProductDataObject dataObject, List<Sku> skus) {
        return Product.restore(
                ProductId.of(dataObject.getId()),
                dataObject.getName(),
                dataObject.getDescription(),
                CategoryId.of(dataObject.getCategoryId()),
                dataObject.getBrand(),
                skus,
                ProductStatus.valueOf(dataObject.getStatus()),
                dataObject.getCreatedAt(),
                dataObject.getUpdatedAt()
        );
    }
    
    /**
     * 保存SKU列表
     */
    private void saveSkus(List<Sku> skus) {
        for (Sku sku : skus) {
            ProductDataMapper.SkuDataObject skuDataObject = convertSkuToDataObject(sku);
            
            if (skuMapper.selectById(skuDataObject.getId()) == null) {
                skuMapper.insert(skuDataObject);
            } else {
                skuMapper.updateById(skuDataObject);
            }
        }
    }
    
    /**
     * SKU领域对象转数据对象
     */
    private ProductDataMapper.SkuDataObject convertSkuToDataObject(Sku sku) {
        String specificationJson = "";
        try {
            if (sku.getSpecification() != null && !sku.getSpecification().isEmpty()) {
                specificationJson = objectMapper.writeValueAsString(sku.getSpecification().getSpecifications());
            }
        } catch (Exception e) {
            specificationJson = "{}";
        }
        
        return new ProductDataMapper.SkuDataObject(
                sku.getId().getValue(),
                sku.getProductId().getValue(),
                sku.getName(),
                specificationJson,
                sku.getPrice().getAmount(),
                sku.getPrice().getCurrency().getCurrencyCode(),
                sku.getBarcode(),
                sku.getStatus().name(),
                sku.getCreatedAt(),
                sku.getUpdatedAt()
        );
    }
    
    /**
     * SKU数据对象转领域对象
     */
    private Sku convertSkuToDomainObject(ProductDataMapper.SkuDataObject dataObject) {
        ProductSpecification specification = ProductSpecification.empty();
        
        try {
            if (dataObject.getSpecificationJson() != null && !dataObject.getSpecificationJson().isEmpty()) {
                Map<String, String> specMap = objectMapper.readValue(
                        dataObject.getSpecificationJson(),
                        new TypeReference<Map<String, String>>() {}
                );
                specification = ProductSpecification.of(specMap);
            }
        } catch (Exception e) {
            specification = ProductSpecification.empty();
        }
        
        return Sku.restore(
                SkuId.of(dataObject.getId()),
                ProductId.of(dataObject.getProductId()),
                dataObject.getName(),
                specification,
                Money.of(dataObject.getPriceAmount(), Currency.getInstance(dataObject.getPriceCurrency())),
                dataObject.getBarcode(),
                SkuStatus.valueOf(dataObject.getStatus()),
                dataObject.getCreatedAt(),
                dataObject.getUpdatedAt()
        );
    }
}