package com.esun.financial.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 使用者實體類別
 * 
 * @author Willy Weng
 * @version 1.0.0
 */
@Entity
@Table(name = "USERS", indexes = {
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_account", columnList = "account")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * 使用者ID (主鍵)
     */
    @Id
    @Column(name = "UserID", length = 50)
    @NotBlank(message = "使用者ID不能為空")
    @Size(max = 50, message = "使用者ID長度不能超過50個字元")
    private String userId;

    /**
     * 使用者名稱
     */
    @Column(name = "UserName", length = 100, nullable = false)
    @NotBlank(message = "使用者名稱不能為空")
    @Size(max = 100, message = "使用者名稱長度不能超過100個字元")
    private String userName;

    /**
     * 使用者電子郵件
     */
    @Column(name = "Email", length = 255, nullable = false, unique = true)
    @NotBlank(message = "電子郵件不能為空")
    @Email(message = "電子郵件格式不正確")
    @Size(max = 255, message = "電子郵件長度不能超過255個字元")
    private String email;

    /**
     * 扣款帳號
     */
    @Column(name = "Account", length = 50, nullable = false)
    @NotBlank(message = "扣款帳號不能為空")
    @Size(max = 50, message = "扣款帳號長度不能超過50個字元")
    private String account;

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
     * 使用者的喜好清單 (一對多關聯)
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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