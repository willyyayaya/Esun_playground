-- 金融商品喜好紀錄系統 - 儲存程序
-- 用於實現安全的資料存取，防止 SQL Injection

USE financial_preference_system;

-- 設定分隔符
DELIMITER $$

-- ================================================
-- 使用者相關儲存程序
-- ================================================

-- 1. 新增使用者
DROP PROCEDURE IF EXISTS sp_InsertUser$$
CREATE PROCEDURE sp_InsertUser(
    IN p_UserID VARCHAR(50),
    IN p_UserName VARCHAR(100),
    IN p_Email VARCHAR(255),
    IN p_Account VARCHAR(50),
    OUT p_Result INT,
    OUT p_Message VARCHAR(500)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
        GET DIAGNOSTICS CONDITION 1
            p_Result = MYSQL_ERRNO, p_Message = MESSAGE_TEXT;
    END;
    
    START TRANSACTION;
    
    -- 檢查用戶是否已存在
    IF EXISTS (SELECT 1 FROM User WHERE UserID = p_UserID OR Email = p_Email) THEN
        SET p_Result = -1;
        SET p_Message = '用戶ID或電子郵件已存在';
        ROLLBACK;
    ELSE
        INSERT INTO User (UserID, UserName, Email, Account)
        VALUES (p_UserID, p_UserName, p_Email, p_Account);
        
        SET p_Result = 0;
        SET p_Message = '用戶新增成功';
        COMMIT;
    END IF;
END$$

-- 2. 查詢使用者資訊
DROP PROCEDURE IF EXISTS sp_GetUser$$
CREATE PROCEDURE sp_GetUser(
    IN p_UserID VARCHAR(50)
)
BEGIN
    SELECT UserID, UserName, Email, Account, CreatedAt, UpdatedAt
    FROM User 
    WHERE UserID = p_UserID;
END$$

-- ================================================
-- 產品相關儲存程序
-- ================================================

-- 3. 新增產品
DROP PROCEDURE IF EXISTS sp_InsertProduct$$
CREATE PROCEDURE sp_InsertProduct(
    IN p_ProductName VARCHAR(200),
    IN p_Price DECIMAL(15,2),
    IN p_FeeRate DECIMAL(5,4),
    OUT p_ProductNo INT,
    OUT p_Result INT,
    OUT p_Message VARCHAR(500)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
        GET DIAGNOSTICS CONDITION 1
            p_Result = MYSQL_ERRNO, p_Message = MESSAGE_TEXT;
    END;
    
    START TRANSACTION;
    
    INSERT INTO Product (ProductName, Price, FeeRate)
    VALUES (p_ProductName, p_Price, p_FeeRate);
    
    SET p_ProductNo = LAST_INSERT_ID();
    SET p_Result = 0;
    SET p_Message = '產品新增成功';
    COMMIT;
END$$

-- 4. 查詢所有產品
DROP PROCEDURE IF EXISTS sp_GetAllProducts$$
CREATE PROCEDURE sp_GetAllProducts()
BEGIN
    SELECT No, ProductName, Price, FeeRate, IsActive, CreatedAt, UpdatedAt
    FROM Product 
    WHERE IsActive = TRUE
    ORDER BY CreatedAt DESC;
END$$

-- 5. 查詢單一產品
DROP PROCEDURE IF EXISTS sp_GetProduct$$
CREATE PROCEDURE sp_GetProduct(
    IN p_ProductNo INT
)
BEGIN
    SELECT No, ProductName, Price, FeeRate, IsActive, CreatedAt, UpdatedAt
    FROM Product 
    WHERE No = p_ProductNo AND IsActive = TRUE;
END$$

-- ================================================
-- 喜好清單相關儲存程序
-- ================================================

-- 6. 新增喜好商品
DROP PROCEDURE IF EXISTS sp_InsertLikeList$$
CREATE PROCEDURE sp_InsertLikeList(
    IN p_UserID VARCHAR(50),
    IN p_ProductNo INT,
    IN p_OrderQuantity INT,
    IN p_Account VARCHAR(50),
    OUT p_SN INT,
    OUT p_Result INT,
    OUT p_Message VARCHAR(500)
)
BEGIN
    DECLARE v_Price DECIMAL(15,2);
    DECLARE v_FeeRate DECIMAL(5,4);
    DECLARE v_TotalAmount DECIMAL(15,2);
    DECLARE v_TotalFee DECIMAL(15,2);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
        GET DIAGNOSTICS CONDITION 1
            p_Result = MYSQL_ERRNO, p_Message = MESSAGE_TEXT;
    END;
    
    START TRANSACTION;
    
    -- 檢查用戶是否存在
    IF NOT EXISTS (SELECT 1 FROM User WHERE UserID = p_UserID) THEN
        SET p_Result = -1;
        SET p_Message = '用戶不存在';
        ROLLBACK;
    -- 檢查產品是否存在且啟用
    ELSEIF NOT EXISTS (SELECT 1 FROM Product WHERE No = p_ProductNo AND IsActive = TRUE) THEN
        SET p_Result = -2;
        SET p_Message = '產品不存在或已停用';
        ROLLBACK;
    -- 檢查是否已經喜歡此商品
    ELSEIF EXISTS (SELECT 1 FROM LikeList WHERE UserID = p_UserID AND ProductNo = p_ProductNo) THEN
        SET p_Result = -3;
        SET p_Message = '已經加入喜好清單，請直接修改數量';
        ROLLBACK;
    ELSE
        -- 取得產品價格和手續費率
        SELECT Price, FeeRate INTO v_Price, v_FeeRate
        FROM Product 
        WHERE No = p_ProductNo;
        
        -- 計算總金額和手續費
        SET v_TotalAmount = v_Price * p_OrderQuantity;
        SET v_TotalFee = v_TotalAmount * v_FeeRate;
        
        INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount)
        VALUES (p_UserID, p_ProductNo, p_OrderQuantity, p_Account, v_TotalFee, v_TotalAmount);
        
        SET p_SN = LAST_INSERT_ID();
        SET p_Result = 0;
        SET p_Message = '商品已加入喜好清單';
        COMMIT;
    END IF;
END$$

-- 7. 查詢用戶喜好清單
DROP PROCEDURE IF EXISTS sp_GetUserLikeList$$
CREATE PROCEDURE sp_GetUserLikeList(
    IN p_UserID VARCHAR(50)
)
BEGIN
    SELECT 
        ll.SN,
        ll.UserID,
        u.UserName,
        u.Email,
        ll.ProductNo,
        p.ProductName,
        p.Price,
        p.FeeRate,
        ll.OrderQuantity,
        ll.Account,
        ll.TotalFee,
        ll.TotalAmount,
        ll.CreatedAt,
        ll.UpdatedAt
    FROM LikeList ll
    INNER JOIN User u ON ll.UserID = u.UserID
    INNER JOIN Product p ON ll.ProductNo = p.No
    WHERE ll.UserID = p_UserID
    ORDER BY ll.CreatedAt DESC;
END$$

-- 8. 查詢用戶喜好清單摘要
DROP PROCEDURE IF EXISTS sp_GetUserLikeListSummary$$
CREATE PROCEDURE sp_GetUserLikeListSummary(
    IN p_UserID VARCHAR(50)
)
BEGIN
    SELECT 
        u.UserName,
        u.Email,
        ll.Account,
        COUNT(ll.SN) as TotalProducts,
        SUM(ll.TotalAmount) as GrandTotalAmount,
        SUM(ll.TotalFee) as GrandTotalFee,
        GROUP_CONCAT(p.ProductName ORDER BY ll.CreatedAt SEPARATOR ', ') as ProductNames
    FROM LikeList ll
    INNER JOIN User u ON ll.UserID = u.UserID
    INNER JOIN Product p ON ll.ProductNo = p.No
    WHERE ll.UserID = p_UserID
    GROUP BY u.UserID, u.UserName, u.Email, ll.Account;
END$$

-- 9. 更新喜好商品
DROP PROCEDURE IF EXISTS sp_UpdateLikeList$$
CREATE PROCEDURE sp_UpdateLikeList(
    IN p_SN INT,
    IN p_OrderQuantity INT,
    IN p_Account VARCHAR(50),
    OUT p_Result INT,
    OUT p_Message VARCHAR(500)
)
BEGIN
    DECLARE v_Price DECIMAL(15,2);
    DECLARE v_FeeRate DECIMAL(5,4);
    DECLARE v_TotalAmount DECIMAL(15,2);
    DECLARE v_TotalFee DECIMAL(15,2);
    DECLARE v_ProductNo INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
        GET DIAGNOSTICS CONDITION 1
            p_Result = MYSQL_ERRNO, p_Message = MESSAGE_TEXT;
    END;
    
    START TRANSACTION;
    
    -- 檢查記錄是否存在
    IF NOT EXISTS (SELECT 1 FROM LikeList WHERE SN = p_SN) THEN
        SET p_Result = -1;
        SET p_Message = '喜好記錄不存在';
        ROLLBACK;
    ELSE
        -- 取得產品編號
        SELECT ProductNo INTO v_ProductNo
        FROM LikeList 
        WHERE SN = p_SN;
        
        -- 取得產品價格和手續費率
        SELECT Price, FeeRate INTO v_Price, v_FeeRate
        FROM Product 
        WHERE No = v_ProductNo;
        
        -- 重新計算總金額和手續費
        SET v_TotalAmount = v_Price * p_OrderQuantity;
        SET v_TotalFee = v_TotalAmount * v_FeeRate;
        
        UPDATE LikeList 
        SET OrderQuantity = p_OrderQuantity,
            Account = p_Account,
            TotalFee = v_TotalFee,
            TotalAmount = v_TotalAmount,
            UpdatedAt = CURRENT_TIMESTAMP
        WHERE SN = p_SN;
        
        SET p_Result = 0;
        SET p_Message = '喜好商品更新成功';
        COMMIT;
    END IF;
END$$

-- 10. 刪除喜好商品
DROP PROCEDURE IF EXISTS sp_DeleteLikeList$$
CREATE PROCEDURE sp_DeleteLikeList(
    IN p_SN INT,
    OUT p_Result INT,
    OUT p_Message VARCHAR(500)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
        GET DIAGNOSTICS CONDITION 1
            p_Result = MYSQL_ERRNO, p_Message = MESSAGE_TEXT;
    END;
    
    START TRANSACTION;
    
    -- 檢查記錄是否存在
    IF NOT EXISTS (SELECT 1 FROM LikeList WHERE SN = p_SN) THEN
        SET p_Result = -1;
        SET p_Message = '喜好記錄不存在';
        ROLLBACK;
    ELSE
        DELETE FROM LikeList WHERE SN = p_SN;
        
        SET p_Result = 0;
        SET p_Message = '喜好商品刪除成功';
        COMMIT;
    END IF;
END$$

-- 11. 記錄系統日誌
DROP PROCEDURE IF EXISTS sp_InsertSystemLog$$
CREATE PROCEDURE sp_InsertSystemLog(
    IN p_UserID VARCHAR(50),
    IN p_Operation VARCHAR(50),
    IN p_TableName VARCHAR(50),
    IN p_RecordID VARCHAR(100),
    IN p_OldValue JSON,
    IN p_NewValue JSON,
    IN p_IPAddress VARCHAR(45)
)
BEGIN
    INSERT INTO SystemLog (UserID, Operation, TableName, RecordID, OldValue, NewValue, IPAddress)
    VALUES (p_UserID, p_Operation, p_TableName, p_RecordID, p_OldValue, p_NewValue, p_IPAddress);
END$$

-- 恢復分隔符
DELIMITER ; 