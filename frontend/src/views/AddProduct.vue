<template>
  <div class="add-product-container">
    <!-- 頁面標題 -->
    <div class="page-header">
      <h1>新增喜好商品</h1>
      <el-button @click="goBack">返回清單</el-button>
    </div>

    <!-- 新增表單 -->
    <el-card class="form-card">
      <el-form 
        :model="form" 
        :rules="rules" 
        ref="formRef" 
        label-width="120px"
        size="large"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="選擇用戶" prop="userId">
              <el-select 
                v-model="form.userId" 
                placeholder="請選擇用戶" 
                style="width: 100%"
              >
                <el-option
                  v-for="user in users"
                  :key="user.userId"
                  :label="user.userName"
                  :value="user.userId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品名稱" prop="productName">
              <el-input 
                v-model="form.productName" 
                placeholder="請輸入商品名稱"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品價格" prop="productPrice">
              <el-input-number 
                v-model="form.productPrice" 
                :min="0" 
                :precision="2"
                style="width: 100%"
                placeholder="請輸入商品價格"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手續費率" prop="feeRate">
              <el-input-number 
                v-model="form.feeRate" 
                :min="0" 
                :max="1" 
                :step="0.001"
                :precision="4"
                style="width: 100%"
                placeholder="請輸入手續費率 (0-1)"
              />
              <div class="form-tip">
                例如：0.001425 表示 0.1425%
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="扣款帳號" prop="accountNumber">
              <el-input 
                v-model="form.accountNumber" 
                placeholder="請輸入扣款帳號"
                maxlength="20"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="購買數量" prop="quantity">
              <el-input-number 
                v-model="form.quantity" 
                :min="1" 
                style="width: 100%"
                placeholder="請輸入購買數量"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 計算結果顯示 -->
        <el-divider content-position="left">費用計算</el-divider>
        
        <div class="calculation-area">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="calc-item">
                <span class="calc-label">商品總價：</span>
                <span class="calc-value">NT$ {{ totalPrice.toLocaleString() }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="calc-item">
                <span class="calc-label">手續費：</span>
                <span class="calc-value">NT$ {{ feeAmount.toLocaleString() }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="calc-item">
                <span class="calc-label">總金額：</span>
                <span class="calc-value total">NT$ {{ totalAmount.toLocaleString() }}</span>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 按鈕區域 -->
        <el-form-item class="button-group">
          <el-button size="large" @click="resetForm">重置</el-button>
          <el-button type="primary" size="large" @click="submitForm" :loading="submitting">
            {{ submitting ? '新增中...' : '確定新增' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 快速選擇商品 -->
    <el-card class="quick-select-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>快速選擇熱門商品</span>
        </div>
      </template>
      
      <div class="quick-products">
        <div 
          v-for="product in quickProducts" 
          :key="product.id"
          class="quick-product-item"
          @click="selectQuickProduct(product)"
        >
          <div class="quick-product-name">{{ product.name }}</div>
          <div class="quick-product-price">NT$ {{ product.price.toLocaleString() }}</div>
          <div class="quick-product-fee">手續費率: {{ (product.feeRate * 100).toFixed(2) }}%</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()

// 響應式資料
const submitting = ref(false)
const formRef = ref()

const users = ref([
  { userId: 1, userName: '張小明' },
  { userId: 2, userName: '李小華' },
  { userId: 3, userName: '王小美' },
  { userId: 4, userName: '陳小強' },
  { userId: 5, userName: '林小雅' }
])

const quickProducts = ref([
  { id: 1, name: '台積電', price: 580.00, feeRate: 0.001425 },
  { id: 2, name: '元大台灣50', price: 145.20, feeRate: 0.001425 },
  { id: 3, name: '鴻海', price: 105.50, feeRate: 0.001425 },
  { id: 4, name: '聯發科', price: 820.00, feeRate: 0.001425 },
  { id: 5, name: '國泰永續高股息', price: 18.45, feeRate: 0.00 }
])

// 表單資料
const form = reactive({
  userId: null,
  productName: '',
  productPrice: 0,
  feeRate: 0,
  accountNumber: '',
  quantity: 1
})

// 表單驗證規則
const rules = {
  userId: [
    { required: true, message: '請選擇用戶', trigger: 'change' }
  ],
  productName: [
    { required: true, message: '請輸入商品名稱', trigger: 'blur' },
    { min: 2, max: 50, message: '商品名稱長度應在 2 到 50 個字符', trigger: 'blur' }
  ],
  productPrice: [
    { required: true, message: '請輸入商品價格', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '商品價格必須大於 0', trigger: 'blur' }
  ],
  feeRate: [
    { required: true, message: '請輸入手續費率', trigger: 'blur' },
    { type: 'number', min: 0, max: 1, message: '手續費率應在 0 到 1 之間', trigger: 'blur' }
  ],
  accountNumber: [
    { required: true, message: '請輸入扣款帳號', trigger: 'blur' },
    { pattern: /^[0-9-]+$/, message: '扣款帳號格式不正確', trigger: 'blur' }
  ],
  quantity: [
    { required: true, message: '請輸入購買數量', trigger: 'blur' },
    { type: 'number', min: 1, message: '購買數量必須大於 0', trigger: 'blur' }
  ]
}

// 計算屬性
const totalPrice = computed(() => {
  return (form.productPrice || 0) * (form.quantity || 0)
})

const feeAmount = computed(() => {
  return totalPrice.value * (form.feeRate || 0)
})

const totalAmount = computed(() => {
  return totalPrice.value + feeAmount.value
})

// 快速選擇商品
const selectQuickProduct = (product) => {
  form.productName = product.name
  form.productPrice = product.price
  form.feeRate = product.feeRate
  ElMessage.success(`已選擇 ${product.name}`)
}

// 提交表單
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    submitting.value = true
    
    const requestData = {
      userId: form.userId,
      productName: form.productName,
      productPrice: form.productPrice,
      feeRate: form.feeRate,
      accountNumber: form.accountNumber,
      quantity: form.quantity
    }
    
    await axios.post('/api/like-list', requestData)
    
    ElMessage.success('新增喜好商品成功！')
    
    // 延遲跳轉，讓用戶看到成功訊息
    setTimeout(() => {
      router.push('/like-list')
    }, 1500)
    
  } catch (error) {
    console.error('新增失敗:', error)
    ElMessage.error('新增喜好商品失敗，請檢查輸入內容')
  } finally {
    submitting.value = false
  }
}

// 重置表單
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(form, {
    userId: null,
    productName: '',
    productPrice: 0,
    feeRate: 0,
    accountNumber: '',
    quantity: 1
  })
  ElMessage.info('表單已重置')
}

// 返回清單
const goBack = () => {
  router.push('/like-list')
}
</script>

<style scoped>
.add-product-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  color: #2c3e50;
}

.form-card {
  margin-bottom: 20px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.calculation-area {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin: 20px 0;
}

.calc-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.calc-label {
  color: #606266;
  font-weight: 500;
}

.calc-value {
  color: #2c3e50;
  font-weight: 600;
  font-size: 1.1em;
}

.calc-value.total {
  color: #e74c3c;
  font-size: 1.2em;
  font-weight: 700;
}

.button-group {
  text-align: center;
  margin-top: 30px;
}

.quick-select-card .card-header {
  font-weight: 600;
  color: #2c3e50;
}

.quick-products {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
}

.quick-product-item {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.quick-product-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
  transform: translateY(-2px);
}

.quick-product-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.quick-product-price {
  color: #e74c3c;
  font-weight: 600;
  margin-bottom: 5px;
}

.quick-product-fee {
  color: #909399;
  font-size: 12px;
}

.el-divider {
  margin: 30px 0 20px 0;
}
</style> 