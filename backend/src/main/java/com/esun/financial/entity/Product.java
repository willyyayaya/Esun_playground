package com.esun.financial.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 產品實體類別
 * 
 * @author 開發團隊
 * @version 1.0.0
 */
@Entity
@Table(name = "Product", indexes = {
    @Index(name = "idx_product_name", columnList = "productName"),
    @Index(name = "idx_price", columnList = "price")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    /**
     * 產品流水號 (主鍵)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "No")
    private Integer no;

    /**
     * 產品名稱
     */
    @Column(name = "ProductName", length = 200, nullable = false)
    @NotBlank(message = "產品名稱不能為空")
    @Size(max = 200, message = "產品名稱長度不能超過200個字元")
    private String productName;

    /**
     * 產品價格
     */
    @Column(name = "Price", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "產品價格不能為空")
    @DecimalMin(value = "0.01", message = "產品價格必須大於0")
    private BigDecimal price;

    /**
     * 手續費率(%)
     */
    @Column(name = "FeeRate", precision = 5, scale = 4, nullable = false)
    @NotNull(message = "手續費率不能為空")
    @DecimalMin(value = "0.0", message = "手續費率不能為負數")
    private BigDecimal feeRate;

    /**
     * 是否啟用
     */
    @Column(name = "IsActive", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * 建立時間
     */
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新時間
     */
    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 產品的喜好清單 (一對多關聯)
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // 避免JSON序列化時的循環參照
    private List<LikeList> likeLists;

    /**
     * 新增前的處理
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * 更新前的處理
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 手動添加 Getter 和 Setter 方法

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<LikeList> getLikeLists() {
        return likeLists;
    }

    public void setLikeLists(List<LikeList> likeLists) {
        this.likeLists = likeLists;
    }
} 