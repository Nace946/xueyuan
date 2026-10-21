<template>
  <el-container class="layout">
    <el-aside width="210px" class="aside">
      <div class="logo">租房招聘通</div>
      <el-menu :default-active="activePath" router class="menu">
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>概览</span>
        </el-menu-item>
        <el-menu-item index="/house">
          <el-icon><House /></el-icon>
          <span>房源管理</span>
        </el-menu-item>
        <el-menu-item index="/job">
          <el-icon><Briefcase /></el-icon>
          <span>招聘管理</span>
        </el-menu-item>
        <el-menu-item index="/banner">
          <el-icon><Picture /></el-icon>
          <span>首页轮播</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <span class="title">{{ currentTitle }}</span>
        <div class="right">
          <span class="user">{{ username }}</span>
          <el-button link type="primary" @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { DataLine, House, Briefcase, Picture } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const username = localStorage.getItem('admin_username') || '管理员'
const activePath = computed(() => '/' + (route.path.split('/')[1] || 'dashboard'))
const currentTitle = computed(() => route.meta.title || '管理后台')

function logout() {
  ElMessageBox.confirm('确认退出登录？', '提示', { type: 'warning' }).then(() => {
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_username')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.layout {
  height: 100vh;
}

.aside {
  background: #ffffff;
  border-right: 1px solid #eceff3;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 18px;
  font-weight: 700;
  color: #2b7de9;
  border-bottom: 1px solid #eceff3;
}

.menu {
  border-right: none;
}

.header {
  background: #ffffff;
  border-bottom: 1px solid #eceff3;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.title {
  font-size: 16px;
  font-weight: 600;
}

.right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user {
  font-size: 14px;
  color: #6b7280;
}

.main {
  padding: 0;
  background: #f5f6f8;
}
</style>
