<template>
  <div id="app">
    <el-container>
      <!-- 頂部導航 -->
      <el-header class="app-header">
        <div class="header-content">
          <div class="logo">
            <el-icon><TrendCharts /></el-icon>
            <span class="title">金融商品喜好紀錄系統</span>
          </div>
          <div class="user-info">
            <el-dropdown>
              <span class="user-name">
                <el-icon><User /></el-icon>
                {{ currentUser }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="switchUser">切換用戶</el-dropdown-item>
                  <el-dropdown-item divided>登出</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <!-- 主要內容區域 -->
      <el-main class="app-main">
        <router-view />
      </el-main>

      <!-- 底部資訊 -->
      <el-footer class="app-footer">
        <div class="footer-content">
          <p>&copy; 2025 金融商品喜好紀錄系統. All rights reserved.</p>
          <p>作者: 翁瑋志</p>
          <p>技術架構: Vue.js + Spring Boot + MySQL</p>
        </div>
      </el-footer>
    </el-container>

    <!-- 切換用戶對話框 -->
    <el-dialog v-model="userDialogVisible" title="選擇用戶" width="400px">
      <el-select
        v-model="selectedUser"
        placeholder="請選擇用戶"
        style="width: 100%"
        size="large"
      >
        <el-option
          v-for="user in users"
          :key="user.value"
          :label="user.label"
          :value="user.value"
        />
      </el-select>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="userDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSwitchUser">確認</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 用戶相關狀態
const currentUser = ref('張三 (USER001)')
const userDialogVisible = ref(false)
const selectedUser = ref('')

// 可選用戶列表
const users = ref([
  { value: 'USER001', label: '張三 (USER001)' },
  { value: 'USER002', label: '李四 (USER002)' },
  { value: 'USER003', label: '王五 (USER003)' },
  { value: 'USER004', label: '趙六 (USER004)' },
  { value: 'USER005', label: '陳七 (USER005)' }
])

// 切換用戶
const switchUser = () => {
  userDialogVisible.value = true
  selectedUser.value = getCurrentUserId()
}

// 確認切換用戶
const confirmSwitchUser = () => {
  if (selectedUser.value) {
    const user = users.value.find(u => u.value === selectedUser.value)
    if (user) {
      currentUser.value = user.label
      localStorage.setItem('currentUser', selectedUser.value)
      localStorage.setItem('currentUserName', user.label)
      router.go(0)
      ElMessage.success('用戶切換成功')
    }
  }
  userDialogVisible.value = false
}

// 獲取當前用戶ID
const getCurrentUserId = () => {
  return localStorage.getItem('currentUser') || 'USER001'
}

// 初始化
onMounted(() => {
  const savedUser = localStorage.getItem('currentUser')
  const savedUserName = localStorage.getItem('currentUserName')
  
  if (savedUser && savedUserName) {
    currentUser.value = savedUserName
  } else {
    localStorage.setItem('currentUser', 'USER001')
    localStorage.setItem('currentUserName', '張三 (USER001)')
  }
})
</script>

<style scoped>
.app-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
}

.logo .el-icon {
  margin-right: 8px;
  font-size: 24px;
}

.title {
  margin-left: 8px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-name {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-name:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.user-name .el-icon {
  margin: 0 4px;
}

.app-main {
  min-height: calc(100vh - 120px);
  background-color: #f5f7fa;
  padding: 20px;
}

.app-footer {
  background-color: #2c3e50;
  color: white;
  text-align: center;
  padding: 20px;
}

.footer-content p {
  margin: 5px 0;
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 10px;
  }
  
  .title {
    display: none;
  }
  
  .app-main {
    padding: 10px;
  }
}
</style> 