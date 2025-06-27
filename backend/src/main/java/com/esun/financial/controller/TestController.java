package com.esun.financial.controller;

import com.esun.financial.entity.Product;
import com.esun.financial.entity.LikeList;
import com.esun.financial.repository.ProductRepository;
import com.esun.financial.repository.LikeListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 測試控制器
 * 用於測試系統功能，不依賴資料庫
 * 
 * @author 開發團隊
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);
    
    private final DataSource dataSource;
    private final ProductRepository productRepository;
    private final LikeListRepository likeListRepository;

    public TestController(DataSource dataSource, ProductRepository productRepository, LikeListRepository likeListRepository) {
        this.dataSource = dataSource;
        this.productRepository = productRepository;
        this.likeListRepository = likeListRepository;
    }

    /**
     * 測試基本API連接
     */
    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "API服務正常運行");
        response.put("timestamp", System.currentTimeMillis());
        
        log.info("收到ping請求");
        return ResponseEntity.ok(response);
    }

    /**
     * 測試資料庫連接
     */
    @GetMapping("/db")
    public ResponseEntity<Map<String, Object>> testDatabase() {
        Map<String, Object> response = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            response.put("status", "success");
            response.put("database", metaData.getDatabaseProductName());
            response.put("version", metaData.getDatabaseProductVersion());
            response.put("url", metaData.getURL());
            response.put("username", metaData.getUserName());
            response.put("message", "資料庫連接成功");
            
            log.info("資料庫連接測試成功: {}", metaData.getDatabaseProductName());
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "資料庫連接失敗: " + e.getMessage());
            log.error("資料庫連接測試失敗", e);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 測試產品資料查詢
     */
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> testProducts() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Product> products = productRepository.findAll();
            
            response.put("status", "success");
            response.put("message", "產品資料查詢成功");
            response.put("count", products.size());
            response.put("data", products);
            
            log.info("查詢到 {} 筆產品資料", products.size());
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "產品資料查詢失敗: " + e.getMessage());
            log.error("產品資料查詢失敗", e);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 測試喜好清單資料查詢
     */
    @GetMapping("/likelist")
    public ResponseEntity<Map<String, Object>> testLikeList() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<LikeList> likeLists = likeListRepository.findAll();
            
            response.put("status", "success");
            response.put("message", "喜好清單資料查詢成功");
            response.put("count", likeLists.size());
            response.put("data", likeLists);
            
            log.info("查詢到 {} 筆喜好清單資料", likeLists.size());
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "喜好清單資料查詢失敗: " + e.getMessage());
            log.error("喜好清單資料查詢失敗", e);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 測試使用者喜好清單查詢
     */
    @GetMapping("/user/{userId}/likelist")
    public ResponseEntity<Map<String, Object>> testUserLikeList(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<LikeList> likeLists = likeListRepository.findByUserIdOrderByCreatedAtDesc(userId);
            
            response.put("status", "success");
            response.put("message", "使用者喜好清單查詢成功");
            response.put("userId", userId);
            response.put("count", likeLists.size());
            response.put("data", likeLists);
            
            log.info("使用者 {} 的喜好清單: {} 筆資料", userId, likeLists.size());
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "使用者喜好清單查詢失敗: " + e.getMessage());
            log.error("使用者喜好清單查詢失敗", e);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 健康檢查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 測試資料庫連接
            try (Connection connection = dataSource.getConnection()) {
                // 測試簡單查詢
                long productCount = productRepository.count();
                long likeListCount = likeListRepository.count();
                
                response.put("status", "UP");
                response.put("database", "連接正常");
                response.put("productCount", productCount);
                response.put("likeListCount", likeListCount);
                response.put("timestamp", System.currentTimeMillis());
                
                log.info("健康檢查通過 - 產品: {}, 喜好清單: {}", productCount, likeListCount);
            }
            
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("error", e.getMessage());
            log.error("健康檢查失敗", e);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 測試新增喜好商品
     */
    @PostMapping("/like-list")
    public ResponseEntity<Map<String, Object>> addLikeList(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 模擬資料驗證
            String productName = (String) request.get("productName");
            Object quantityObj = request.get("quantity");
            String accountNumber = (String) request.get("accountNumber");
            
            if (productName == null || productName.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "商品名稱不能為空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (quantityObj == null) {
                response.put("success", false);
                response.put("message", "購買數量不能為空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "扣款帳號不能為空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 模擬成功回應
            response.put("success", true);
            response.put("message", "商品已成功加入喜好清單");
            response.put("data", request);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "新增失敗: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 測試查詢喜好清單
     */
    @GetMapping("/like-list/{userId}")
    public ResponseEntity<Map<String, Object>> getLikeList(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            response.put("success", true);
            response.put("message", "查詢成功");
            response.put("data", new Object[]{}); // 返回空陣列
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查詢失敗: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 測試更新喜好商品
     */
    @PutMapping("/like-list/{likeListId}")
    public ResponseEntity<Map<String, Object>> updateLikeList(
            @PathVariable Integer likeListId, 
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (likeListId == null || likeListId <= 0) {
                response.put("success", false);
                response.put("message", "無效的記錄ID");
                return ResponseEntity.badRequest().body(response);
            }
            
            response.put("success", true);
            response.put("message", "喜好商品更新成功");
            response.put("data", request);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新失敗: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 測試刪除喜好商品
     */
    @DeleteMapping("/like-list/{likeListId}")
    public ResponseEntity<Map<String, Object>> deleteLikeList(@PathVariable Integer likeListId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (likeListId == null || likeListId <= 0) {
                response.put("success", false);
                response.put("message", "無效的記錄ID");
                return ResponseEntity.badRequest().body(response);
            }
            
            response.put("success", true);
            response.put("message", "喜好商品刪除成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "刪除失敗: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 載入完整測試資料（包含使用者、產品、喜好清單）
     */
    @PostMapping("/load-full-test-data")
    public ResponseEntity<Map<String, Object>> loadFullTestData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("開始載入完整測試資料...");
            
            // 先清空現有資料
            likeListRepository.deleteAll();
            productRepository.deleteAll();
            
            // 清空使用者資料
            try (Connection connection = dataSource.getConnection()) {
                try (var statement = connection.prepareStatement("DELETE FROM USERS")) {
                    statement.executeUpdate();
                }
            }
            
            int usersLoaded = 0;
            int productsLoaded = 0;
            int likeListsLoaded = 0;
            
            // 載入使用者資料
            String[] userInserts = {
                "INSERT INTO USERS (UserID, UserName, Email, Account, CreatedAt, UpdatedAt) VALUES ('USER001', '張三', 'zhang.san@example.com', '123-456-789', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO USERS (UserID, UserName, Email, Account, CreatedAt, UpdatedAt) VALUES ('USER002', '李四', 'li.si@example.com', '234-567-890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO USERS (UserID, UserName, Email, Account, CreatedAt, UpdatedAt) VALUES ('USER003', '王五', 'wang.wu@example.com', '345-678-901', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO USERS (UserID, UserName, Email, Account, CreatedAt, UpdatedAt) VALUES ('USER004', '趙六', 'zhao.liu@example.com', '456-789-012', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO USERS (UserID, UserName, Email, Account, CreatedAt, UpdatedAt) VALUES ('USER005', '陳七', 'chen.qi@example.com', '567-890-123', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
            };
            
            try (Connection connection = dataSource.getConnection()) {
                for (String sql : userInserts) {
                    try (var statement = connection.prepareStatement(sql)) {
                        statement.executeUpdate();
                        usersLoaded++;
                    }
                }
            }
            
            // 載入產品資料
            Product[] products = {
                createProduct("台積電股票 (2330)", 580.00, 0.001425),
                createProduct("聯發科股票 (2454)", 820.00, 0.001425),
                createProduct("鴻海股票 (2317)", 105.50, 0.001425),
                createProduct("台達電股票 (2308)", 350.00, 0.001425),
                createProduct("中華電信股票 (2412)", 125.50, 0.001425),
                createProduct("元大台灣50 ETF (0050)", 145.80, 0.0020),
                createProduct("富邦科技 ETF (0052)", 65.20, 0.0020),
                createProduct("元大高股息 ETF (0056)", 35.80, 0.0020),
                createProduct("國泰永續高息 ETF (00878)", 18.50, 0.0020),
                createProduct("台美科技基金", 25.80, 0.015),
                createProduct("新興市場債券基金", 12.50, 0.015),
                createProduct("全球股票基金", 18.90, 0.015),
                createProduct("黃金存摺", 1850.00, 0.0025),
                createProduct("美元定存", 1.00, 0.001),
                createProduct("日圓定存", 1.00, 0.001)
            };
            
            for (Product product : products) {
                productRepository.save(product);
                productsLoaded++;
            }
            
            // 載入喜好清單資料
            String[] likeListInserts = {
                // 張三的喜好清單
                "INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount, CreatedAt, UpdatedAt) VALUES ('USER001', 1, 1000, '123-456-789', 826.50, 580000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount, CreatedAt, UpdatedAt) VALUES ('USER001', 6, 500, '123-456-789', 145.80, 72900.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount, CreatedAt, UpdatedAt) VALUES ('USER001', 10, 100, '123-456-789', 38.70, 2580.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                
                // 李四的喜好清單
                "INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount, CreatedAt, UpdatedAt) VALUES ('USER002', 2, 500, '234-567-890', 584.25, 410000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount, CreatedAt, UpdatedAt) VALUES ('USER002', 3, 2000, '234-567-890', 300.68, 211000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount, CreatedAt, UpdatedAt) VALUES ('USER002', 13, 10, '234-567-890', 46.25, 18500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                
                // 王五的喜好清單
                "INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount, CreatedAt, UpdatedAt) VALUES ('USER003', 4, 300, '345-678-901', 149.63, 105000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount, CreatedAt, UpdatedAt) VALUES ('USER003', 8, 1000, '345-678-901', 71.60, 35800.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount, CreatedAt, UpdatedAt) VALUES ('USER003', 12, 50, '345-678-901', 14.18, 945.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                
                // 趙六的喜好清單
                "INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount, CreatedAt, UpdatedAt) VALUES ('USER004', 5, 800, '456-789-012', 142.97, 100400.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount, CreatedAt, UpdatedAt) VALUES ('USER004', 9, 2000, '456-789-012', 74.00, 37000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount, CreatedAt, UpdatedAt) VALUES ('USER004', 14, 10000, '456-789-012', 10.00, 10000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                
                // 陳七的喜好清單
                "INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount, CreatedAt, UpdatedAt) VALUES ('USER005', 7, 1500, '567-890-123', 195.60, 97800.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount, CreatedAt, UpdatedAt) VALUES ('USER005', 11, 80, '567-890-123', 15.00, 1000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO LikeList (UserID, ProductNo, OrderQuantity, Account, TotalFee, TotalAmount, CreatedAt, UpdatedAt) VALUES ('USER005', 15, 50000, '567-890-123', 50.00, 50000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
            };
            
            try (Connection connection = dataSource.getConnection()) {
                for (String sql : likeListInserts) {
                    try (var statement = connection.prepareStatement(sql)) {
                        statement.executeUpdate();
                        likeListsLoaded++;
                    }
                }
            }
            
            log.info("成功載入完整測試資料 - 使用者: {}, 產品: {}, 喜好清單: {}", usersLoaded, productsLoaded, likeListsLoaded);
            
            response.put("status", "success");
            response.put("message", "完整測試資料載入成功");
            response.put("usersLoaded", usersLoaded);
            response.put("productsLoaded", productsLoaded);
            response.put("likeListsLoaded", likeListsLoaded);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "完整測試資料載入失敗: " + e.getMessage());
            log.error("完整測試資料載入失敗", e);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 手動載入測試資料（僅產品）
     */
    @PostMapping("/load-test-data")
    public ResponseEntity<Map<String, Object>> loadTestData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 先清空現有資料
            likeListRepository.deleteAll();
            productRepository.deleteAll();
            
            log.info("開始載入產品測試資料...");
            
            // 載入產品資料（與 test_data.sql 一致）
            Product[] products = {
                createProduct("台積電股票 (2330)", 580.00, 0.001425),
                createProduct("聯發科股票 (2454)", 820.00, 0.001425),
                createProduct("鴻海股票 (2317)", 105.50, 0.001425),
                createProduct("台達電股票 (2308)", 350.00, 0.001425),
                createProduct("中華電信股票 (2412)", 125.50, 0.001425),
                createProduct("元大台灣50 ETF (0050)", 145.80, 0.0020),
                createProduct("富邦科技 ETF (0052)", 65.20, 0.0020),
                createProduct("元大高股息 ETF (0056)", 35.80, 0.0020),
                createProduct("國泰永續高息 ETF (00878)", 18.50, 0.0020),
                createProduct("台美科技基金", 25.80, 0.015),
                createProduct("新興市場債券基金", 12.50, 0.015),
                createProduct("全球股票基金", 18.90, 0.015),
                createProduct("黃金存摺", 1850.00, 0.0025),
                createProduct("美元定存", 1.00, 0.001),
                createProduct("日圓定存", 1.00, 0.001)
            };
            
            for (Product product : products) {
                productRepository.save(product);
            }
            
            log.info("成功載入 {} 筆產品資料", products.length);
            
            response.put("status", "success");
            response.put("message", "產品測試資料載入成功");
            response.put("productsLoaded", products.length);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "產品測試資料載入失敗: " + e.getMessage());
            log.error("產品測試資料載入失敗", e);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 載入使用者測試資料
     */
    @PostMapping("/load-users")
    public ResponseEntity<Map<String, Object>> loadUsers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("開始載入使用者測試資料...");
            
            // 使用與 test_data.sql 一致的資料
            String[] userInserts = {
                "INSERT INTO USERS (UserID, UserName, Email, Account, CreatedAt, UpdatedAt) VALUES ('USER001', '張三', 'zhang.san@example.com', '123-456-789', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO USERS (UserID, UserName, Email, Account, CreatedAt, UpdatedAt) VALUES ('USER002', '李四', 'li.si@example.com', '234-567-890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO USERS (UserID, UserName, Email, Account, CreatedAt, UpdatedAt) VALUES ('USER003', '王五', 'wang.wu@example.com', '345-678-901', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO USERS (UserID, UserName, Email, Account, CreatedAt, UpdatedAt) VALUES ('USER004', '趙六', 'zhao.liu@example.com', '456-789-012', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "INSERT INTO USERS (UserID, UserName, Email, Account, CreatedAt, UpdatedAt) VALUES ('USER005', '陳七', 'chen.qi@example.com', '567-890-123', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
            };
            
            try (Connection connection = dataSource.getConnection()) {
                for (String sql : userInserts) {
                    try (var statement = connection.prepareStatement(sql)) {
                        statement.executeUpdate();
                    }
                }
            }
            
            log.info("成功載入 {} 筆使用者資料", userInserts.length);
            
            response.put("status", "success");
            response.put("message", "使用者資料載入成功");
            response.put("usersLoaded", userInserts.length);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "使用者資料載入失敗: " + e.getMessage());
            log.error("使用者資料載入失敗", e);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 查詢所有使用者
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sql = "SELECT UserID, UserName, Email, Account FROM USERS ORDER BY UserID";
            List<Map<String, Object>> users = new ArrayList<>();
            
            try (Connection connection = dataSource.getConnection();
                 var statement = connection.prepareStatement(sql);
                 var resultSet = statement.executeQuery()) {
                
                while (resultSet.next()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("userId", resultSet.getString("UserID"));
                    user.put("userName", resultSet.getString("UserName"));
                    user.put("email", resultSet.getString("Email"));
                    user.put("account", resultSet.getString("Account"));
                    users.add(user);
                }
            }
            
            response.put("status", "success");
            response.put("message", "查詢成功");
            response.put("data", users);
            
            log.info("查詢到 {} 筆使用者資料", users.size());
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "查詢使用者失敗: " + e.getMessage());
            log.error("查詢使用者失敗", e);
        }
        
        return ResponseEntity.ok(response);
    }

    private Product createProduct(String name, double price, double feeRate) {
        Product product = new Product();
        product.setProductName(name);
        product.setPrice(java.math.BigDecimal.valueOf(price));
        product.setFeeRate(java.math.BigDecimal.valueOf(feeRate));
        product.setIsActive(true);
        return product;
    }
} 