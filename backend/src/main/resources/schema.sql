-- 金融商品喜好紀錄系統 - H2資料庫表結構
-- 建立時間: 2024
-- 資料庫: H2 Database

-- 1. 使用者表 (改名為USERS避免H2保留字衝突)
CREATE TABLE IF NOT EXISTS USERS (
    UserID VARCHAR(50) PRIMARY KEY,
    UserName VARCHAR(100) NOT NULL,
    Email VARCHAR(255) NOT NULL UNIQUE,
    Account VARCHAR(50) NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 建立索引
CREATE INDEX IF NOT EXISTS idx_user_email ON USERS(Email);
CREATE INDEX IF NOT EXISTS idx_user_account ON USERS(Account);

-- 2. 產品資料表
CREATE TABLE IF NOT EXISTS Product (
    No INT AUTO_INCREMENT PRIMARY KEY,
    ProductName VARCHAR(200) NOT NULL,
    Price DECIMAL(15,2) NOT NULL CHECK (Price > 0),
    FeeRate DECIMAL(5,4) NOT NULL CHECK (FeeRate >= 0 AND FeeRate <= 1),
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 建立索引
CREATE INDEX IF NOT EXISTS idx_product_name ON Product(ProductName);
CREATE INDEX IF NOT EXISTS idx_product_price ON Product(Price);

-- 3. 喜好清單表
CREATE TABLE IF NOT EXISTS LikeList (
    SN INT AUTO_INCREMENT PRIMARY KEY,
    UserID VARCHAR(50) NOT NULL,
    ProductNo INT NOT NULL,
    OrderQuantity INT NOT NULL DEFAULT 1 CHECK (OrderQuantity > 0),
    Account VARCHAR(50) NOT NULL,
    TotalFee DECIMAL(15,2) NOT NULL CHECK (TotalFee >= 0),
    TotalAmount DECIMAL(15,2) NOT NULL CHECK (TotalAmount > 0),
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (UserID) REFERENCES USERS(UserID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ProductNo) REFERENCES Product(No) ON DELETE CASCADE ON UPDATE CASCADE,
    
    -- 確保同一用戶不會重複喜歡同一商品
    UNIQUE (UserID, ProductNo)
);

-- 建立索引
CREATE INDEX IF NOT EXISTS idx_likelist_user_product ON LikeList(UserID, ProductNo);
CREATE INDEX IF NOT EXISTS idx_likelist_account ON LikeList(Account);
CREATE INDEX IF NOT EXISTS idx_likelist_total_amount ON LikeList(TotalAmount);

-- 4. 系統日誌表
CREATE TABLE IF NOT EXISTS SystemLog (
    LogID INT AUTO_INCREMENT PRIMARY KEY,
    UserID VARCHAR(50),
    Operation VARCHAR(50) NOT NULL,
    TableName VARCHAR(50) NOT NULL,
    RecordID VARCHAR(100),
    OldValue CLOB,
    NewValue CLOB,
    OperationTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    IPAddress VARCHAR(45)
);

-- 建立索引
CREATE INDEX IF NOT EXISTS idx_systemlog_user_operation ON SystemLog(UserID, Operation);
CREATE INDEX IF NOT EXISTS idx_systemlog_operation_time ON SystemLog(OperationTime);
CREATE INDEX IF NOT EXISTS idx_systemlog_table_name ON SystemLog(TableName); 