package com.esun.financial.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 喜好清單實體類別
 * 
 * @author Willy Weng
 * @version 1.0.0
 */
@Entity
@Table(name = "LikeList", indexes = {
    @Index(name = "idx_user_product", columnList = "userID, productNo"),
    @Index(name = "idx_account", columnList = "account"),
    @Index(name = "idx_total_amount", columnList = "totalAmount")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_product", columnNames = {"userID", "productNo"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeList {

    /**
     * 流水序號 (主鍵)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SN")
    private Integer sn;

    /**
     * 使用者ID (外鍵)
     */
    @Column(name = "UserID", length = 50, nullable = false)
    @NotBlank(message = "使用者ID不能為空")
    @Size(max = 50, message = "使用者ID長度不能超過50個字元")
    private String userId;

    /**
     * 產品流水號 (外鍵)
     */
    @Column(name = "ProductNo", nullable = false)
    @NotNull(message = "產品編號不能為空")
    private Integer productNo;

    /**
     * 購買數量
     */
    @Column(name = "OrderQuantity", nullable = false)
    @NotNull(message = "購買數量不能為空")
    @Min(value = 1, message = "購買數量必須大於0")
    @Builder.Default
    private Integer orderQuantity = 1;

    /**
     * 扣款帳號
     */
    @Column(name = "Account", length = 50, nullable = false)
    @NotBlank(message = "扣款帳號不能為空")
    @Size(max = 50, message = "扣款帳號長度不能超過50個字元")
    private String account;

    /**
     * 總手續費用(台幣計價)
     */
    @Column(name = "TotalFee", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "總手續費用不能為空")
    @DecimalMin(value = "0.0", message = "總手續費用不能為負數")
    private BigDecimal totalFee;

    /**
     * 預計扣款總金額
     */
    @Column(name = "TotalAmount", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "預計扣款總金額不能為空")
    @DecimalMin(value = "0.01", message = "預計扣款總金額必須大於0")
    private BigDecimal totalAmount;

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
     * 關聯的使用者 (多對一關聯)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", referencedColumnName = "UserID", insertable = false, updatable = false)
    private User user;

    /**
     * 關聯的產品 (多對一關聯)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProductNo", referencedColumnName = "No", insertable = false, updatable = false)
    private Product product;

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

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getProductNo() {
        return productNo;
    }

    public void setProductNo(Integer productNo) {
        this.productNo = productNo;
    }

    public Integer getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(Integer orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
} 