package com.ecommerce.product.domain.repository;

import com.ecommerce.product.domain.entity.Product;
import com.ecommerce.product.domain.entity.ProductStatus;
import com.ecommerce.product.domain.valueobject.CategoryId;
import com.ecommerce.product.domain.valueobject.ProductId;

import java.util.List;
import java.util.Optional;

/**
 * 商品仓储接口 - 领域层
 * 职责：定义商品数据访问的领域接口，与基础设施层解耦
 * 特性：面向领域对象、业务导向的查询方法
 */
public interface ProductRepository {
    
    /**
     * 保存商品（新增或更新）
     */
    Product save(Product product);
    
    /**
     * 根据ID查找商品
     */
    Optional<Product> findById(ProductId productId);
    
    /**
     * 根据ID查找商品（包含SKU信息）
     */
    Optional<Product> findByIdWithSkus(ProductId productId);
    
    /**
     * 检查商品是否存在
     */
    boolean existsById(ProductId productId);
    
    /**
     * 根据名称查找商品
     */
    List<Product> findByName(String name);
    
    /**
     * 根据名称模糊查找商品
     */
    List<Product> findByNameContaining(String keyword);
    
    /**
     * 根据分类查找商品
     */
    List<Product> findByCategoryId(CategoryId categoryId);
    
    /**
     * 根据分类查找商品（包含子分类）
     */
    List<Product> findByCategoryIdStartingWith(String categoryPath);
    
    /**
     * 根据品牌查找商品
     */
    List<Product> findByBrand(String brand);
    
    /**
     * 根据状态查找商品
     */
    List<Product> findByStatus(ProductStatus status);
    
    /**
     * 根据多个条件查找商品
     */
    List<Product> findByCriteria(String keyword, CategoryId categoryId, String brand, ProductStatus status);
    
    /**
     * 分页查询商品
     */
    List<Product> findAll(int page, int size);
    
    /**
     * 分页查询商品（根据条件）
     */
    List<Product> findByCriteria(String keyword, CategoryId categoryId, String brand, 
                                ProductStatus status, int page, int size);
    
    /**
     * 统计商品数量
     */
    long count();
    
    /**
     * 根据条件统计商品数量
     */
    long countByCriteria(String keyword, CategoryId categoryId, String brand, ProductStatus status);
    
    /**
     * 根据分类统计商品数量
     */
    long countByCategoryId(CategoryId categoryId);
    
    /**
     * 根据品牌统计商品数量
     */
    long countByBrand(String brand);
    
    /**
     * 根据状态统计商品数量
     */
    long countByStatus(ProductStatus status);
    
    /**
     * 查找热销商品（需要业务系统提供销量数据）
     */
    List<Product> findPopularProducts(int limit);
    
    /**
     * 查找推荐商品
     */
    List<Product> findRecommendedProducts(CategoryId categoryId, int limit);
    
    /**
     * 查找最新商品
     */
    List<Product> findLatestProducts(int limit);
    
    /**
     * 删除商品（物理删除，慎用）
     */
    void deleteById(ProductId productId);
    
    /**
     * 批量保存商品
     */
    List<Product> saveAll(List<Product> products);
    
    /**
     * 批量查询商品
     */
    List<Product> findByIds(List<ProductId> productIds);
    
    /**
     * 检查商品名称是否重复
     */
    boolean existsByNameAndIdNot(String name, ProductId excludeId);
    
    /**
     * 获取分类下的商品数量
     */
    long getProductCountByCategory(CategoryId categoryId);
    
    /**
     * 查找需要更新的商品（用于定时任务）
     */
    List<Product> findProductsNeedingUpdate();
    
    /**
     * 搜索商品（综合搜索）
     */
    List<Product> searchProducts(String keyword, CategoryId categoryId, ProductStatus status, int offset, int pageSize);
}