package com.esun.financial.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 喜好清單請求 DTO
 * 
 * @author 開發團隊
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeListRequest {

    /**
     * 使用者ID
     */
    @NotBlank(message = "使用者ID不能為空")
    @Size(max = 50, message = "使用者ID長度不能超過50個字元")
    private String userId;

    /**
     * 產品編號
     */
    private Integer productNo;

    /**
     * 購買數量
     */
    @NotNull(message = "購買數量不能為空")
    @Min(value = 1, message = "購買數量必須大於0")
    private Integer orderQuantity;

    /**
     * 扣款帳號
     */
    @NotBlank(message = "扣款帳號不能為空")
    @Size(max = 50, message = "扣款帳號長度不能超過50個字元")
    private String account;

    // 新增前端需要的欄位
    /**
     * 商品名稱
     */
    private String productName;

    /**
     * 商品價格
     */
    private Double productPrice;

    /**
     * 手續費率
     */
    private Double feeRate;

    /**
     * 購買數量（前端使用）
     */
    private Integer quantity;

    /**
     * 扣款帳號（前端使用）
     */
    private String accountNumber;

    // 手動添加 Getter 和 Setter 方法
    
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public Double getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(Double feeRate) {
        this.feeRate = feeRate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
} 