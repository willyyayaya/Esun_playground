-- 金融商品喜好紀錄系統 - 資料庫表結構
-- 建立時間: 2025
-- 資料庫: MySQL 8.0+

-- 建立資料庫 (如果不存在)
CREATE DATABASE IF NOT EXISTS financial_preference_system 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE financial_preference_system;

-- 1. 使用者表
DROP TABLE IF EXISTS User;
CREATE TABLE User (
    UserID VARCHAR(50) PRIMARY KEY COMMENT '使用者ID',
    UserName VARCHAR(100) NOT NULL COMMENT '使用者名稱',
    Email VARCHAR(255) NOT NULL UNIQUE COMMENT '使用者電子郵件',
    Account VARCHAR(50) NOT NULL COMMENT '扣款帳號',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
    
    INDEX idx_email (Email),
    INDEX idx_account (Account)
) COMMENT='使用者資料表';

-- 2. 產品資料表
DROP TABLE IF EXISTS Product;
CREATE TABLE Product (
    No INT AUTO_INCREMENT PRIMARY KEY COMMENT '產品流水號',
    ProductName VARCHAR(200) NOT NULL COMMENT '產品名稱',
    Price DECIMAL(15,2) NOT NULL COMMENT '產品價格',
    FeeRate DECIMAL(5,4) NOT NULL COMMENT '手續費率(%)',
    IsActive BOOLEAN DEFAULT TRUE COMMENT '是否啟用',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
    
    INDEX idx_product_name (ProductName),
    INDEX idx_price (Price),
    CONSTRAINT chk_price_positive CHECK (Price > 0),
    CONSTRAINT chk_fee_rate_valid CHECK (FeeRate >= 0 AND FeeRate <= 1)
) COMMENT='產品資料表';

-- 3. 喜好清單表 (修正設計，加入外鍵關聯)
DROP TABLE IF EXISTS LikeList;
CREATE TABLE LikeList (
    SN INT AUTO_INCREMENT PRIMARY KEY COMMENT '流水序號',
    UserID VARCHAR(50) NOT NULL COMMENT '使用者ID',
    ProductNo INT NOT NULL COMMENT '產品流水號',
    OrderQuantity INT NOT NULL DEFAULT 1 COMMENT '購買數量',
    Account VARCHAR(50) NOT NULL COMMENT '扣款帳號',
    TotalFee DECIMAL(15,2) NOT NULL COMMENT '總手續費用(台幣計價)',
    TotalAmount DECIMAL(15,2) NOT NULL COMMENT '預計扣款總金額',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
    
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ProductNo) REFERENCES Product(No) ON DELETE CASCADE ON UPDATE CASCADE,
    
    INDEX idx_user_product (UserID, ProductNo),
    INDEX idx_account (Account),
    INDEX idx_total_amount (TotalAmount),
    
    CONSTRAINT chk_order_quantity_positive CHECK (OrderQuantity > 0),
    CONSTRAINT chk_total_fee_positive CHECK (TotalFee >= 0),
    CONSTRAINT chk_total_amount_positive CHECK (TotalAmount > 0),
    
    -- 確保同一用戶不會重複喜歡同一商品
    UNIQUE KEY uk_user_product (UserID, ProductNo)
) COMMENT='喜好清單表';

-- 4. 系統日誌表 (用於記錄操作歷史)
DROP TABLE IF EXISTS SystemLog;
CREATE TABLE SystemLog (
    LogID INT AUTO_INCREMENT PRIMARY KEY COMMENT '日誌ID',
    UserID VARCHAR(50) COMMENT '操作用戶ID',
    Operation VARCHAR(50) NOT NULL COMMENT '操作類型 (INSERT/UPDATE/DELETE)',
    TableName VARCHAR(50) NOT NULL COMMENT '操作表名',
    RecordID VARCHAR(100) COMMENT '記錄ID',
    OldValue JSON COMMENT '舊值',
    NewValue JSON COMMENT '新值',
    OperationTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作時間',
    IPAddress VARCHAR(45) COMMENT 'IP地址',
    
    INDEX idx_user_operation (UserID, Operation),
    INDEX idx_operation_time (OperationTime),
    INDEX idx_table_name (TableName)
) COMMENT='系統操作日誌表'; 