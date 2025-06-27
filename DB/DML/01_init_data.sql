-- 金融商品喜好紀錄系統 - 初始化資料
-- 用於提供測試和展示用的範例資料

USE financial_preference_system;

-- 停用外鍵檢查 (便於資料插入)
SET FOREIGN_KEY_CHECKS = 0;

-- ================================================
-- 清空現有資料 (測試用)
-- ================================================
DELETE FROM SystemLog;
DELETE FROM LikeList;
DELETE FROM Product;
DELETE FROM User;

-- ================================================
-- 使用者測試資料
-- ================================================
INSERT INTO User (UserID, UserName, Email, Account) VALUES
('USER001', '張三', 'zhang.san@example.com', '123-456-789'),
('USER002', '李四', 'li.si@example.com', '234-567-890'),
('USER003', '王五', 'wang.wu@example.com', '345-678-901'),
('USER004', '趙六', 'zhao.liu@example.com', '456-789-012'),
('USER005', '陳七', 'chen.qi@example.com', '567-890-123');

-- ================================================
-- 金融商品測試資料
-- ================================================
INSERT INTO Product (ProductName, Price, FeeRate) VALUES
('台積電股票 (2330)', 580.00, 0.001425),  -- 手續費率 0.1425%
('聯發科股票 (2454)', 820.00, 0.001425),
('鴻海股票 (2317)', 105.50, 0.001425),
('台達電股票 (2308)', 350.00, 0.001425),
('中華電信股票 (2412)', 125.50, 0.001425),
('元大台灣50 ETF (0050)', 145.80, 0.0020),  -- ETF手續費率 0.2%
('富邦科技 ETF (0052)', 65.20, 0.0020),
('元大高股息 ETF (0056)', 35.80, 0.0020),
('國泰永續高息 ETF (00878)', 18.50, 0.0020),
('台美科技基金', 25.80, 0.015),  -- 基金手續費率 1.5%
('新興市場債券基金', 12.50, 0.015),
('全球股票基金', 18.90, 0.015),
('黃金存摺', 1850.00, 0.0025),  -- 每公克價格，手續費率 0.25%
('美元定存', 1.00, 0.001),  -- 1美元，手續費率 0.1%
('日圓定存', 1.00, 0.001);  -- 1日圓，手續費率 0.1%

-- ================================================
-- 喜好清單測試資料
-- ================================================
-- 張三的喜好清單
INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount) VALUES
('USER001', 1, 1000, '123-456-789', 826.50, 580000.00),  -- 台積電 1000股
('USER001', 6, 500, '123-456-789', 145.80, 72900.00),    -- 0050 500股
('USER001', 10, 100, '123-456-789', 38.70, 2580.00);     -- 台美科技基金 100單位

-- 李四的喜好清單  
INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount) VALUES
('USER002', 2, 500, '234-567-890', 584.25, 410000.00),   -- 聯發科 500股
('USER002', 3, 2000, '234-567-890', 300.68, 211000.00),  -- 鴻海 2000股
('USER002', 13, 10, '234-567-890', 46.25, 18500.00);     -- 黃金存摺 10公克

-- 王五的喜好清單
INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount) VALUES
('USER003', 4, 300, '345-678-901', 149.63, 105000.00),   -- 台達電 300股
('USER003', 8, 1000, '345-678-901', 71.60, 35800.00),    -- 0056 1000股
('USER003', 12, 50, '345-678-901', 14.18, 945.00);       -- 全球股票基金 50單位

-- 趙六的喜好清單
INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount) VALUES
('USER004', 5, 800, '456-789-012', 142.97, 100400.00),   -- 中華電信 800股
('USER004', 9, 2000, '456-789-012', 74.00, 37000.00),    -- 00878 2000股
('USER004', 14, 10000, '456-789-012', 10.00, 10000.00);  -- 美元定存 10000美元

-- 陳七的喜好清單
INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount) VALUES
('USER005', 7, 1500, '567-890-123', 195.60, 97800.00),   -- 0052 1500股
('USER005', 11, 80, '567-890-123', 15.00, 1000.00),      -- 新興市場債券基金 80單位
('USER005', 15, 50000, '567-890-123', 50.00, 50000.00);  -- 日圓定存 50000日圓

-- ================================================
-- 系統日誌測試資料
-- ================================================
INSERT INTO SystemLog (UserID, Operation, TableName, RecordID, NewValue, IPAddress) VALUES
('USER001', 'INSERT', 'LikeList', '1', '{"ProductNo":1,"OrderQuantity":1000}', '192.168.1.100'),
('USER001', 'INSERT', 'LikeList', '2', '{"ProductNo":6,"OrderQuantity":500}', '192.168.1.100'),
('USER002', 'INSERT', 'LikeList', '4', '{"ProductNo":2,"OrderQuantity":500}', '192.168.1.101'),
('USER003', 'INSERT', 'LikeList', '7', '{"ProductNo":4,"OrderQuantity":300}', '192.168.1.102'),
('USER004', 'INSERT', 'LikeList', '10', '{"ProductNo":5,"OrderQuantity":800}', '192.168.1.103'),
('USER005', 'INSERT', 'LikeList', '13', '{"ProductNo":7,"OrderQuantity":1500}', '192.168.1.104');

-- 重新啟用外鍵檢查
SET FOREIGN_KEY_CHECKS = 1;

-- ================================================
-- 資料驗證查詢
-- ================================================
-- 查看插入的資料統計
SELECT '使用者數量' as 項目, COUNT(*) as 數量 FROM User
UNION ALL
SELECT '產品數量', COUNT(*) FROM Product
UNION ALL  
SELECT '喜好清單數量', COUNT(*) FROM LikeList
UNION ALL
SELECT '系統日誌數量', COUNT(*) FROM SystemLog;

-- 查看每個使用者的喜好商品摘要
SELECT 
    u.UserName as 使用者姓名,
    u.Email as 電子郵件,
    COUNT(ll.SN) as 喜好商品數,
    FORMAT(SUM(ll.TotalAmount), 2) as 總投資金額,
    FORMAT(SUM(ll.TotalFee), 2) as 總手續費
FROM User u
LEFT JOIN LikeList ll ON u.UserID = ll.UserID
GROUP BY u.UserID, u.UserName, u.Email
ORDER BY SUM(ll.TotalAmount) DESC; 