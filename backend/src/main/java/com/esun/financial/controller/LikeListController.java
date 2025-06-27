package com.esun.financial.controller;

import com.esun.financial.dto.LikeListRequest;
import com.esun.financial.dto.LikeListResponse;
import com.esun.financial.entity.Product;
import com.esun.financial.service.LikeListService;
import com.esun.financial.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * 喜好清單控制器
 * 提供金融商品喜好清單的RESTful API
 * 
 * @author Willy Weng
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/likelist")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class LikeListController {

    private static final Logger log = LoggerFactory.getLogger(LikeListController.class);
    
    private final LikeListService likeListService;
    private final ProductRepository productRepository;

    public LikeListController(LikeListService likeListService, ProductRepository productRepository) {
        this.likeListService = likeListService;
        this.productRepository = productRepository;
    }

    /**
     * 查詢使用者喜好清單
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<LikeListResponse>>> getUserLikeList(@PathVariable String userId) {
        try {
            log.info("查詢使用者喜好清單: {}", userId);
            List<LikeListResponse> likeList = likeListService.getUserLikeList(userId);
            
            return ResponseEntity.ok(ApiResponse.<List<LikeListResponse>>builder()
                    .success(true)
                    .message("查詢成功")
                    .data(likeList)
                    .build());
        } catch (Exception e) {
            log.error("查詢喜好清單失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<LikeListResponse>>builder()
                            .success(false)
                            .message("查詢失敗: " + e.getMessage())
                            .build());
        }
    }

    /**
     * 新增喜好商品
     */
    @PostMapping
    public ResponseEntity<ApiResponse<LikeListResponse>> addLikeList(@Valid @RequestBody LikeListRequest request) {
        try {
            log.info("新增喜好商品: {}", request);
            LikeListResponse response = likeListService.addLikeList(request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<LikeListResponse>builder()
                            .success(true)
                            .message("新增成功")
                            .data(response)
                            .build());
        } catch (Exception e) {
            log.error("新增喜好商品失敗", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<LikeListResponse>builder()
                            .success(false)
                            .message("新增失敗: " + e.getMessage())
                            .build());
        }
    }

    /**
     * 更新喜好商品
     */
    @PutMapping("/{sn}")
    public ResponseEntity<ApiResponse<LikeListResponse>> updateLikeList(
            @PathVariable Integer sn, 
            @Valid @RequestBody LikeListRequest request) {
        try {
            log.info("更新喜好商品: SN={}, Request={}", sn, request);
            LikeListResponse response = likeListService.updateLikeList(sn, request);
            
            return ResponseEntity.ok(ApiResponse.<LikeListResponse>builder()
                    .success(true)
                    .message("更新成功")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("更新喜好商品失敗", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<LikeListResponse>builder()
                            .success(false)
                            .message("更新失敗: " + e.getMessage())
                            .build());
        }
    }

    /**
     * 刪除喜好商品
     */
    @DeleteMapping("/{sn}")
    public ResponseEntity<ApiResponse<String>> deleteLikeList(@PathVariable Integer sn) {
        try {
            log.info("刪除喜好商品: SN={}", sn);
            likeListService.deleteLikeList(sn);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("刪除成功")
                    .build());
        } catch (Exception e) {
            log.error("刪除喜好商品失敗", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .message("刪除失敗: " + e.getMessage())
                            .build());
        }
    }

    /**
     * 查詢所有產品
     */
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        try {
            log.info("查詢所有產品");
            List<Product> products = productRepository.findByIsActiveTrue();
            
            return ResponseEntity.ok(ApiResponse.<List<Product>>builder()
                    .success(true)
                    .message("查詢成功")
                    .data(products)
                    .build());
        } catch (Exception e) {
            log.error("查詢產品失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<Product>>builder()
                            .success(false)
                            .message("查詢失敗: " + e.getMessage())
                            .build());
        }
    }

    /**
     * 查詢所有使用者
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllUsers() {
        try {
            log.info("查詢所有使用者");
            
            // 建立測試使用者資料
            List<Map<String, Object>> users = new ArrayList<>();
            
            String[] userNames = {"張三", "李四", "王五", "趙六", "陳七"};
            String[] userIds = {"USER001", "USER002", "USER003", "USER004", "USER005"};
            String[] emails = {"zhang.san@example.com", "li.si@example.com", "wang.wu@example.com", "zhao.liu@example.com", "chen.qi@example.com"};
            String[] accounts = {"123-456-789", "234-567-890", "345-678-901", "456-789-012", "567-890-123"};
            
            for (int i = 0; i < userNames.length; i++) {
                Map<String, Object> user = new HashMap<>();
                user.put("userId", userIds[i]);
                user.put("userName", userNames[i]);
                user.put("email", emails[i]);
                user.put("account", accounts[i]);
                users.add(user);
            }
            
            return ResponseEntity.ok(ApiResponse.<List<Map<String, Object>>>builder()
                    .success(true)
                    .message("查詢成功")
                    .data(users)
                    .build());
        } catch (Exception e) {
            log.error("查詢使用者失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<Map<String, Object>>>builder()
                            .success(false)
                            .message("查詢失敗: " + e.getMessage())
                            .build());
        }
    }

    /**
     * 統一回應格式
     */
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public ApiResponse() {}

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public static <T> ApiResponseBuilder<T> builder() {
            return new ApiResponseBuilder<>();
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }

        public static class ApiResponseBuilder<T> {
            private boolean success;
            private String message;
            private T data;

            public ApiResponseBuilder<T> success(boolean success) {
                this.success = success;
                return this;
            }

            public ApiResponseBuilder<T> message(String message) {
                this.message = message;
                return this;
            }

            public ApiResponseBuilder<T> data(T data) {
                this.data = data;
                return this;
            }

            public ApiResponse<T> build() {
                return new ApiResponse<>(success, message, data);
            }
        }
    }
} 