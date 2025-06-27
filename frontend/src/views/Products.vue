<template>
  <div class="products-container">
    <!-- 頁面標題 -->
    <div class="page-header">
      <h1>金融商品瀏覽</h1>
    </div>

    <!-- 搜尋區域 -->
    <div class="search-area">
      <el-input
        v-model="searchKeyword"
        placeholder="搜尋商品名稱或類型"
        style="width: 300px"
        @input="filterProducts"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select 
        v-model="selectedType" 
        placeholder="選擇商品類型" 
        @change="filterProducts"
        style="width: 200px; margin-left: 10px"
        clearable
      >
        <el-option label="全部類型" value="" />
        <el-option label="股票" value="股票" />
        <el-option label="ETF" value="ETF" />
        <el-option label="基金" value="基金" />
        <el-option label="債券" value="債券" />
      </el-select>
    </div>

    <!-- 商品卡片網格 -->
    <div class="products-grid">
      <div 
        v-for="product in filteredProducts" 
        :key="product.productId"
        class="product-card"
      >
        <div class="product-header">
          <h3>{{ product.productName }}</h3>
          <el-tag :type="getTypeColor(product.productType)">
            {{ product.productType }}
          </el-tag>
        </div>
        
        <div class="product-content">
          <div class="product-info">
            <div class="info-item">
              <span class="label">價格：</span>
              <span class="value price">NT$ {{ product.productPrice.toLocaleString() }}</span>
            </div>
            <div class="info-item">
              <span class="label">手續費率：</span>
              <span class="value">{{ (product.feeRate * 100).toFixed(2) }}%</span>
            </div>
            <div class="info-item">
              <span class="label">風險等級：</span>
              <span class="value">{{ product.riskLevel }}</span>
            </div>
          </div>
          
          <div class="product-description">
            <p>{{ product.description }}</p>
          </div>
        </div>
        
        <div class="product-actions">
          <el-button type="primary" @click="addToLikeList(product)">
            加入喜好清單
          </el-button>
          <el-button @click="viewDetails(product)">
            查看詳情
          </el-button>
        </div>
      </div>
    </div>

    <!-- 商品詳情對話框 -->
    <el-dialog 
      title="商品詳情" 
      v-model="detailDialogVisible" 
      width="600px"
    >
      <div v-if="selectedProduct" class="product-detail">
        <div class="detail-header">
          <h2>{{ selectedProduct.productName }}</h2>
          <el-tag :type="getTypeColor(selectedProduct.productType)" size="large">
            {{ selectedProduct.productType }}
          </el-tag>
        </div>
        
        <div class="detail-content">
          <div class="detail-row">
            <span class="detail-label">商品代碼：</span>
            <span class="detail-value">{{ selectedProduct.productCode }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">目前價格：</span>
            <span class="detail-value price">NT$ {{ selectedProduct.productPrice.toLocaleString() }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">手續費率：</span>
            <span class="detail-value">{{ (selectedProduct.feeRate * 100).toFixed(2) }}%</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">風險等級：</span>
            <span class="detail-value">{{ selectedProduct.riskLevel }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">商品描述：</span>
            <p class="detail-description">{{ selectedProduct.description }}</p>
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false">關閉</el-button>
        <el-button type="primary" @click="addToLikeList(selectedProduct)">
          加入喜好清單
        </el-button>
      </template>
    </el-dialog>

    <!-- 加入喜好清單對話框 -->
    <el-dialog 
      title="加入喜好清單" 
      v-model="addDialogVisible" 
      width="500px"
    >
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="120px">
        <el-form-item label="選擇用戶" prop="userId">
          <el-select v-model="addForm.userId" placeholder="請選擇用戶" style="width: 100%">
            <el-option
              v-for="user in users"
              :key="user.userId"
              :label="user.userName"
              :value="user.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="扣款帳號" prop="accountNumber">
          <el-input v-model="addForm.accountNumber" placeholder="請輸入扣款帳號" />
        </el-form-item>
        <el-form-item label="購買數量" prop="quantity">
          <el-input-number 
            v-model="addForm.quantity" 
            :min="1" 
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddForm">確定加入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import axios from 'axios'

// 響應式資料
const products = ref([])
const filteredProducts = ref([])
const searchKeyword = ref('')
const selectedType = ref('')
const detailDialogVisible = ref(false)
const addDialogVisible = ref(false)
const selectedProduct = ref(null)

const users = ref([
  { userId: 1, userName: '張小明' },
  { userId: 2, userName: '李小華' },
  { userId: 3, userName: '王小美' },
  { userId: 4, userName: '陳小強' },
  { userId: 5, userName: '林小雅' }
])

// 加入喜好清單表單
const addForm = reactive({
  userId: null,
  productName: '',
  productPrice: 0,
  feeRate: 0,
  accountNumber: '',
  quantity: 1
})

const addRules = {
  userId: [
    { required: true, message: '請選擇用戶', trigger: 'change' }
  ],
  accountNumber: [
    { required: true, message: '請輸入扣款帳號', trigger: 'blur' }
  ],
  quantity: [
    { required: true, message: '請輸入購買數量', trigger: 'blur' }
  ]
}

const addFormRef = ref()

// 載入商品資料
const loadProducts = async () => {
  try {
    // 模擬商品資料，實際應該從API獲取
    const mockProducts = [
      {
        productId: 1,
        productCode: '2330',
        productName: '台積電',
        productType: '股票',
        productPrice: 580.00,
        feeRate: 0.001425,
        riskLevel: '中',
        description: '全球最大的晶圓代工廠，主要從事半導體晶圓代工服務'
      },
      {
        productId: 2,
        productCode: '0050',
        productName: '元大台灣50',
        productType: 'ETF',
        productPrice: 145.20,
        feeRate: 0.001425,
        riskLevel: '中',
        description: '追蹤台灣50指數表現的ETF，投資台灣市值前50大公司'
      },
      {
        productId: 3,
        productCode: '2317',
        productName: '鴻海',
        productType: '股票',
        productPrice: 105.50,
        feeRate: 0.001425,
        riskLevel: '中高',
        description: '全球最大的電子製造服務商，主要從事電腦、通訊及消費性電子產品製造'
      },
      {
        productId: 4,
        productCode: '2454',
        productName: '聯發科',
        productType: '股票',
        productPrice: 820.00,
        feeRate: 0.001425,
        riskLevel: '高',
        description: '全球IC設計領導廠商，專精於無線通訊及數位多媒體晶片'
      },
      {
        productId: 5,
        productCode: '00878',
        productName: '國泰永續高股息',
        productType: 'ETF',
        productPrice: 18.45,
        feeRate: 0.00,
        riskLevel: '中',
        description: '以永續投資為主軸的高股息ETF，聚焦ESG與高股息'
      }
    ]
    
    products.value = mockProducts
    filteredProducts.value = mockProducts
  } catch (error) {
    console.error('載入商品資料失敗:', error)
    ElMessage.error('載入商品資料失敗')
  }
}

// 篩選商品
const filterProducts = () => {
  let result = products.value
  
  // 按關鍵字篩選
  if (searchKeyword.value) {
    result = result.filter(product => 
      product.productName.includes(searchKeyword.value) ||
      product.productType.includes(searchKeyword.value) ||
      product.productCode.includes(searchKeyword.value)
    )
  }
  
  // 按類型篩選
  if (selectedType.value) {
    result = result.filter(product => product.productType === selectedType.value)
  }
  
  filteredProducts.value = result
}

// 獲取類型顏色
const getTypeColor = (type) => {
  const colorMap = {
    '股票': 'primary',
    'ETF': 'success',
    '基金': 'warning',
    '債券': 'info'
  }
  return colorMap[type] || 'default'
}

// 查看商品詳情
const viewDetails = (product) => {
  selectedProduct.value = product
  detailDialogVisible.value = true
}

// 加入喜好清單
const addToLikeList = (product) => {
  selectedProduct.value = product
  addForm.productName = product.productName
  addForm.productPrice = product.productPrice
  addForm.feeRate = product.feeRate
  addForm.accountNumber = ''
  addForm.quantity = 1
  addDialogVisible.value = true
}

// 提交加入喜好清單
const submitAddForm = async () => {
  if (!addFormRef.value) return
  
  try {
    await addFormRef.value.validate()
    
    const requestData = {
      userId: addForm.userId,
      productNo: selectedProduct.value.productId,
      account: addForm.accountNumber,
      orderQuantity: addForm.quantity
    }
    
    await axios.post('/api/likelist', requestData)
    ElMessage.success('成功加入喜好清單')
    addDialogVisible.value = false
    detailDialogVisible.value = false
  } catch (error) {
    console.error('加入喜好清單失敗:', error)
    ElMessage.error('加入喜好清單失敗')
  }
}

// 組件掛載時載入資料
onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.products-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  color: #2c3e50;
}

.search-area {
  margin-bottom: 30px;
  display: flex;
  align-items: center;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.product-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease;
}

.product-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.product-header h3 {
  margin: 0;
  color: #2c3e50;
}

.product-content {
  margin-bottom: 20px;
}

.product-info {
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.label {
  color: #606266;
  font-weight: 500;
}

.value {
  color: #2c3e50;
  font-weight: 600;
}

.price {
  color: #e74c3c;
  font-size: 1.1em;
}

.product-description p {
  color: #606266;
  line-height: 1.5;
  margin: 0;
}

.product-actions {
  display: flex;
  gap: 10px;
}

.product-detail {
  padding: 10px 0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.detail-header h2 {
  margin: 0;
  color: #2c3e50;
}

.detail-content {
  line-height: 1.8;
}

.detail-row {
  display: flex;
  margin-bottom: 12px;
}

.detail-label {
  width: 120px;
  color: #606266;
  font-weight: 500;
}

.detail-value {
  color: #2c3e50;
  font-weight: 600;
  flex: 1;
}

.detail-description {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}
</style> 