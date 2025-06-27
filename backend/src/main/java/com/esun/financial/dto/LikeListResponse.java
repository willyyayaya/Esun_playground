package com.esun.financial.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 喜好清單回應 DTO
 * 
 * @author Willy Weng
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeListResponse {

    /**
     * 流水序號
     */
    private Integer sn;

    /**
     * 使用者ID
     */
    private String userId;

    /**
     * 使用者名稱
     */
    private String userName;

    /**
     * 使用者電子郵件
     */
    private String email;

    /**
     * 產品編號
     */
    private Integer productNo;

    /**
     * 產品名稱
     */
    private String productName;

    /**
     * 產品價格
     */
    private BigDecimal price;

    /**
     * 手續費率
     */
    private BigDecimal feeRate;

    /**
     * 購買數量
     */
    private Integer orderQuantity;

    /**
     * 扣款帳號
     */
    private String account;

    /**
     * 總手續費用(台幣計價)
     */
    private BigDecimal totalFee;

    /**
     * 預計扣款總金額
     */
    private BigDecimal totalAmount;

    /**
     * 建立時間
     */
    private LocalDateTime createdAt;

    /**
     * 更新時間
     */
    private LocalDateTime updatedAt;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getProductNo() {
        return productNo;
    }

    public void setProductNo(Integer productNo) {
        this.productNo = productNo;
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
} 