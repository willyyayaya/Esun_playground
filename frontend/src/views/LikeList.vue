<template>
  <div class="like-list-container">
    <!-- 頁面標題 -->
    <div class="page-header">
      <h1>我的喜好清單</h1>
      <el-button type="primary" @click="showAddDialog" icon="Plus">
        新增喜好商品
      </el-button>
    </div>

    <!-- 用戶選擇器 -->
    <div class="user-selector">
      <el-select 
        v-model="selectedUserId" 
        placeholder="選擇用戶" 
        @change="loadLikeList"
        style="width: 200px"
      >
        <el-option
          v-for="user in users"
          :key="user.userId"
          :label="user.userName"
          :value="user.userId"
        />
      </el-select>
    </div>

    <!-- 資料表格 -->
    <el-table 
      :data="likeList" 
      v-loading="loading"
      style="width: 100%"
      stripe
      border
    >
      <el-table-column prop="productName" label="商品名稱" width="200" />
      <el-table-column prop="productPrice" label="價格" width="120">
        <template #default="scope">
          NT$ {{ scope.row.productPrice.toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column prop="feeRate" label="手續費率" width="100">
        <template #default="scope">
          {{ (scope.row.feeRate * 100).toFixed(2) }}%
        </template>
      </el-table-column>
      <el-table-column prop="accountNumber" label="扣款帳號" width="150" />
      <el-table-column prop="quantity" label="購買數量" width="100" />
      <el-table-column prop="totalAmount" label="總金額" width="120">
        <template #default="scope">
          NT$ {{ scope.row.totalAmount.toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column prop="feeAmount" label="手續費" width="120">
        <template #default="scope">
          NT$ {{ scope.row.feeAmount.toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="建立時間" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="editItem(scope.row)">編輯</el-button>
          <el-button size="small" type="danger" @click="deleteItem(scope.row)">刪除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/編輯對話框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="選擇商品" prop="productId" v-if="!form.likeListId">
          <el-select 
            v-model="form.productId" 
            placeholder="請選擇商品" 
            @change="onProductChange"
            style="width: 100%"
          >
            <el-option
              v-for="product in products"
              :key="product.no"
              :label="`${product.productName} - NT$ ${product.price.toLocaleString()}`"
              :value="product.no"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商品資訊" v-if="form.productId || form.likeListId">
          <div class="product-info">
            <p><strong>商品名稱：</strong>{{ form.productName }}</p>
            <p><strong>價格：</strong>NT$ {{ form.productPrice.toLocaleString() }}</p>
            <p><strong>手續費率：</strong>{{ (form.feeRate * 100).toFixed(2) }}%</p>
          </div>
        </el-form-item>
        <el-form-item label="扣款帳號" prop="accountNumber">
          <el-input v-model="form.accountNumber" placeholder="請輸入扣款帳號" />
        </el-form-item>
        <el-form-item label="購買數量" prop="quantity">
          <el-input-number 
            v-model="form.quantity" 
            :min="1" 
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="預計金額" v-if="(form.productId || form.likeListId) && form.quantity">
          <div class="amount-info">
            <p><strong>總金額：</strong>NT$ {{ (form.productPrice * form.quantity).toLocaleString() }}</p>
            <p><strong>手續費：</strong>NT$ {{ (form.productPrice * form.quantity * form.feeRate).toLocaleString() }}</p>
            <p><strong>合計：</strong>NT$ {{ ((form.productPrice * form.quantity) + (form.productPrice * form.quantity * form.feeRate)).toLocaleString() }}</p>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">確定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

// 響應式資料
const loading = ref(false)
const dialogVisible = ref(false)
const selectedUserId = ref('')
const likeList = ref([])
const users = ref([])
const products = ref([])

// 表單資料
const form = reactive({
  likeListId: null,
  productId: null,
  productName: '',
  productPrice: 0,
  feeRate: 0,
  accountNumber: '',
  quantity: 1
})

// 表單驗證規則
const rules = {
  productId: [
    { required: true, message: '請選擇商品', trigger: 'change' }
  ],
  accountNumber: [
    { required: true, message: '請輸入扣款帳號', trigger: 'blur' }
  ],
  quantity: [
    { required: true, message: '請輸入購買數量', trigger: 'blur' }
  ]
}

const formRef = ref()
const dialogTitle = ref('新增喜好商品')

// 載入喜好清單
const loadLikeList = async () => {
  if (!selectedUserId.value) return
  
  loading.value = true
  try {
    const response = await axios.get(`http://localhost:8080/api/likelist/${selectedUserId.value}`)
    if (response.data.success) {
      // 轉換後端資料格式到前端格式
      likeList.value = response.data.data.map(item => ({
        likeListId: item.sn,
        productName: item.productName,
        productPrice: item.price,
        feeRate: item.feeRate,
        accountNumber: item.account,
        quantity: item.orderQuantity,
        totalAmount: item.totalAmount,
        feeAmount: item.totalFee,
        createTime: item.createdAt
      }))
    } else {
      console.error('載入喜好清單失敗:', response.data.message)
      ElMessage.error(response.data.message || '載入喜好清單失敗')
      likeList.value = []
    }
  } catch (error) {
    console.error('載入喜好清單失敗:', error)
    ElMessage.error('載入喜好清單失敗')
    likeList.value = []
  } finally {
    loading.value = false
  }
}

// 顯示新增對話框
const showAddDialog = () => {
  dialogTitle.value = '新增喜好商品'
  resetForm()
  dialogVisible.value = true
}

// 編輯項目
const editItem = (item) => {
  dialogTitle.value = '編輯喜好商品'
  Object.assign(form, {
    likeListId: item.likeListId,
    productId: null, // 編輯時不需要選擇產品
    productName: item.productName,
    productPrice: item.productPrice,
    feeRate: item.feeRate,
    accountNumber: item.accountNumber,
    quantity: item.quantity
  })
  dialogVisible.value = true
}

// 刪除項目
const deleteItem = async (item) => {
  try {
    await ElMessageBox.confirm(
      `確定要刪除商品「${item.productName}」嗎？`,
      '確認刪除',
      {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await axios.delete(`http://localhost:8080/api/likelist/${item.likeListId}`)
    if (response.data.success) {
      ElMessage.success('刪除成功')
      loadLikeList()
    } else {
      ElMessage.error(response.data.message || '刪除失敗')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('刪除失敗:', error)
      ElMessage.error('刪除失敗')
    }
  }
}

// 提交表單
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    // 動態驗證規則：新增時需要選擇產品，編輯時不需要
    const currentRules = { ...rules }
    if (form.likeListId) {
      delete currentRules.productId
    }
    
    // 臨時設定驗證規則
    formRef.value.clearValidate()
    await formRef.value.validate()
    
    const requestData = {
      userId: selectedUserId.value,
      productNo: form.productId || null,
      account: form.accountNumber,
      orderQuantity: form.quantity
    }
    
    let response
    if (form.likeListId) {
      // 更新現有項目
      response = await axios.put(`http://localhost:8080/api/likelist/${form.likeListId}`, requestData)
    } else {
      // 新增項目 - 確保有選擇產品
      if (!form.productId) {
        ElMessage.error('請選擇商品')
        return
      }
      response = await axios.post('http://localhost:8080/api/likelist', requestData)
    }
    
    if (response.data.success) {
      ElMessage.success(form.likeListId ? '更新成功' : '新增成功')
      dialogVisible.value = false
      loadLikeList()
    } else {
      ElMessage.error(response.data.message || '操作失敗')
    }
  } catch (error) {
    console.error('提交失敗:', error)
    ElMessage.error('操作失敗')
  }
}

// 重置表單
const resetForm = () => {
  Object.assign(form, {
    likeListId: null,
    productId: null,
    productName: '',
    productPrice: 0,
    feeRate: 0,
    accountNumber: '',
    quantity: 1
  })
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 產品選擇變更處理
const onProductChange = (productId) => {
  if (productId) {
    const selectedProduct = products.value.find(p => p.no === productId)
    if (selectedProduct) {
      form.productName = selectedProduct.productName
      form.productPrice = selectedProduct.price
      form.feeRate = selectedProduct.feeRate
    }
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-TW')
}

// 載入使用者列表
const loadUsers = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/likelist/users')
    if (response.data.success) {
      users.value = response.data.data
      // 設定預設選中第一個使用者
      if (users.value.length > 0) {
        selectedUserId.value = users.value[0].userId
      }
    } else {
      console.error('載入使用者失敗:', response.data.message)
      ElMessage.error('載入使用者失敗')
    }
  } catch (error) {
    console.error('載入使用者失敗:', error)
    ElMessage.error('載入使用者失敗')
  }
}

// 載入產品列表
const loadProducts = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/likelist/products')
    if (response.data.success) {
      products.value = response.data.data
    } else {
      console.error('載入產品失敗:', response.data.message)
      ElMessage.error('載入產品失敗')
    }
  } catch (error) {
    console.error('載入產品失敗:', error)
    ElMessage.error('載入產品失敗')
  }
}

// 組件掛載時載入資料
onMounted(async () => {
  await loadUsers()
  await loadProducts()
  loadLikeList()
})
</script>

<style scoped>
.like-list-container {
  padding: 20px;
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

.user-selector {
  margin-bottom: 20px;
}

.el-table {
  margin-top: 20px;
}

.product-info, .amount-info {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.product-info p, .amount-info p {
  margin: 5px 0;
  color: #606266;
}

.amount-info p:last-child {
  font-size: 16px;
  font-weight: bold;
  color: #409eff;
  border-top: 1px solid #e4e7ed;
  padding-top: 10px;
  margin-top: 10px;
}
</style> 