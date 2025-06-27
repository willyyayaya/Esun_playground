import { createRouter, createWebHistory } from 'vue-router'

// 路由配置
const routes = [
  {
    path: '/',
    name: 'Home',
    redirect: '/like-list'
  },
  {
    path: '/like-list',
    name: 'LikeList',
    component: () => import('../views/LikeList.vue'),
    meta: {
      title: '我的喜好清單'
    }
  },
  {
    path: '/products',
    name: 'Products',
    component: () => import('../views/Products.vue'),
    meta: {
      title: '金融商品瀏覽'
    }
  },
  {
    path: '/add-product',
    name: 'AddProduct',
    component: () => import('../views/AddProduct.vue'),
    meta: {
      title: '新增喜好商品'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守衛
router.beforeEach((to, from, next) => {
  // 設置頁面標題
  if (to.meta.title) {
    document.title = `${to.meta.title} - 金融商品喜好紀錄系統`
  }
  next()
})

export default router 