# 金融商品喜好紀錄系統

這是一個基於 Spring Boot 後端和 Vue.js 前端的金融商品喜好管理系統，採用三層式架構設計。

## 系統功能

1. **新增喜好金融商品** - 支援產品名稱、價格、手續費率、扣款帳號、購買數量
2. **查詢喜好金融商品清單** - 包含用戶資訊、總金額、手續費等完整資訊
3. **刪除喜好金融商品** - 移除不需要的商品記錄
4. **更改喜好金融商品資訊** - 修改商品資訊和購買數量
5. **用戶切換功能** - 支援多用戶測試

## 技術架構

### 前端 (Vue.js)
- **框架**: Vue.js 3 + Composition API
- **UI 組件**: Element Plus
- **HTTP 客戶端**: Axios
- **建置工具**: Vite
- **資料持久化**: localStorage (測試模式)

### 後端 (Spring Boot)
- **框架**: Spring Boot 2.7.18
- **安全性**: Spring Security
- **資料驗證**: Bean Validation
- **日誌**: SLF4J + Logback
- **建置工具**: Maven
- **API 設計**: RESTful

### 資料庫設計
- **關聯式資料庫**: MySQL (已準備 DDL/DML)
- **儲存程序**: 11個完整的 CRUD 操作程序
- **安全性**: 防 SQL Injection 設計

## 當前運行模式

⚠️ **測試模式說明**：
- 前端使用 localStorage 進行資料持久化
- 後端提供完整 API 但暫不連接資料庫
- 所有 CRUD 功能均可正常測試

## 專案結構

```
esun_playground/
├── backend/                    # Spring Boot 後端
│   ├── src/main/java/com/esun/financial/
│   │   ├── controller/         # REST API 控制器
│   │   ├── service/           # 業務邏輯層
│   │   ├── repository/        # 資料存取層
│   │   ├── entity/           # JPA 實體類別
│   │   └── dto/              # 資料傳輸對象
│   ├── src/main/resources/
│   │   └── application.yml    # 應用程式配置
│   └── pom.xml               # Maven 依賴配置
├── frontend/                  # Vue.js 前端
│   ├── src/
│   │   ├── views/            # 頁面組件
│   │   ├── router/           # 路由配置
│   │   └── style.css         # 全域樣式
│   ├── package.json          # npm 依賴配置
│   └── vite.config.js        # Vite 建置配置
└── DB/                       # 資料庫腳本 (生產環境用)
    ├── DDL/                  # 資料表定義
    │   ├── 01_create_tables.sql
    │   └── 02_create_procedures.sql
    └── DML/                  # 初始化資料
        └── 01_init_data.sql
```

## 快速開始

### 1. 後端啟動
```bash
cd backend
mvn clean compile -DskipTests
mvn spring-boot:run
```
- 服務地址: http://localhost:8080
- Spring Security 預設帳號: `user`
- 密碼會在啟動時自動生成並顯示在控制台

### 2. 前端啟動
```bash
cd frontend
npm install
npm run dev
```
- 前端地址: http://localhost:5173
- 支援熱重載開發模式

## API 端點

### 測試端點 (`/api/test`)
- `GET /api/test/health` - 健康檢查
- `POST /api/test/like-list` - 測試新增喜好商品
- `GET /api/test/like-list/{userId}` - 測試查詢喜好清單
- `PUT /api/test/like-list/{likeListId}` - 測試更新喜好商品
- `DELETE /api/test/like-list/{likeListId}` - 測試刪除喜好商品

### 正式端點 (`/api/like-list`)
- `POST /api/like-list` - 新增喜好商品
- `GET /api/like-list/{userId}` - 查詢喜好清單
- `PUT /api/like-list/{likeListId}` - 更新喜好商品
- `DELETE /api/like-list/{likeListId}` - 刪除喜好商品

## 系統特色

### 安全性設計
- **SQL Injection 防護**: 使用 Stored Procedure 和參數化查詢
- **XSS 防護**: 前後端輸入驗證和輸出編碼
- **CSRF 防護**: Spring Security 預設啟用
- **輸入驗證**: Bean Validation 註解驗證

### 用戶體驗
- **響應式設計**: 支援桌面和移動設備
- **即時計算**: 自動計算總金額和手續費
- **用戶切換**: 支援多用戶測試環境
- **資料持久化**: 本地儲存保證資料不丟失

### 開發特性
- **熱重載**: 前後端都支援開發時熱重載
- **完整日誌**: 詳細的操作和錯誤日誌
- **錯誤處理**: 完善的異常處理機制
- **代碼品質**: 使用 Lombok 減少樣板代碼

## 測試資料

系統預設包含以下測試資料：
- **用戶**: 5個測試用戶 (user1-user5)
- **金融商品**: 15種台灣金融商品
  - 台積電 (2330)
  - 元大台灣50 (0050)
  - 富邦台50 (006208)
  - 等等...

## 生產環境部署

### 資料庫設置 (生產環境)
1. 建立 MySQL 資料庫
2. 執行 `DB/DDL/01_create_tables.sql`
3. 執行 `DB/DDL/02_create_procedures.sql`
4. 執行 `DB/DML/01_init_data.sql`
5. 更新 `application.yml` 中的資料庫連接設定

### 建置和部署
```bash
# 後端建置
cd backend
mvn clean package -DskipTests

# 前端建置
cd frontend
npm run build
```

## 開發團隊

- **架構設計**: 三層式架構 (Presentation + Business + Data)
- **開發語言**: Java 11+, JavaScript ES6+
- **開發環境**: IntelliJ IDEA, VS Code
- **版本控制**: Git

## 授權

本專案僅供學習和展示使用。 