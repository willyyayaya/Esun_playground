package com.esun.financial.repository;

import com.esun.financial.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 產品資料存取介面
 * 
 * @author 開發團隊
 * @version 1.0.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    /**
     * 查詢啟用的產品列表
     * 
     * @return 啟用的產品列表
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.createdAt DESC")
    List<Product> findActiveProducts();

    /**
     * 根據產品名稱模糊查詢
     * 
     * @param productName 產品名稱
     * @return 產品列表
     */
    @Query("SELECT p FROM Product p WHERE p.productName LIKE %:productName% AND p.isActive = true")
    List<Product> findByProductNameContaining(@Param("productName") String productName);

    /**
     * 查詢指定價格範圍的產品
     * 
     * @param minPrice 最低價格
     * @param maxPrice 最高價格
     * @return 產品列表
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isActive = true")
    List<Product> findByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice, 
                                   @Param("maxPrice") java.math.BigDecimal maxPrice);

    /**
     * 查詢所有啟用的產品
     */
    List<Product> findByIsActiveTrue();

    /**
     * 根據產品名稱模糊查詢啟用的產品
     */
    @Query("SELECT p FROM Product p WHERE p.productName LIKE %:name% AND p.isActive = true")
    List<Product> findByProductNameContainingAndIsActiveTrue(@Param("name") String name);

    /**
     * 根據價格範圍查詢啟用的產品
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isActive = true ORDER BY p.price")
    List<Product> findByPriceRangeAndIsActiveTrue(@Param("minPrice") java.math.BigDecimal minPrice, 
                                                  @Param("maxPrice") java.math.BigDecimal maxPrice);
} 