<template>
  <div class="login-page">
    <div class="login-box">
      <div class="login-title">租房招聘通 · 管理后台</div>
      <div class="login-sub">首次启动后使用默认账号登录，登录后请及时修改密码</div>

      <el-form :model="form" :rules="rules" ref="formRef" size="large" @keyup.enter="onSubmit">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入账号" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>
        <el-button type="primary" class="login-btn" :loading="loading" @click="onSubmit">登 录</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '../api/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = ref({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function onSubmit() {
  formRef.value.validate((valid) => {
    if (!valid) return
    loading.value = true
    login(form.value)
      .then((data) => {
        localStorage.setItem('admin_token', data.token)
        localStorage.setItem('admin_username', (data.user && data.user.username) || form.value.username)
        ElMessage.success('登录成功')
        router.push('/dashboard')
      })
      .finally(() => {
        loading.value = false
      })
  })
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2b7de9, #4f9bf5);
}

.login-box {
  width: 380px;
  background: #ffffff;
  border-radius: 12px;
  padding: 36px 32px 28px;
  box-shadow: 0 12px 40px rgba(20, 40, 80, 0.18);
}

.login-title {
  font-size: 20px;
  font-weight: 700;
  text-align: center;
  color: #22262c;
}

.login-sub {
  margin: 8px 0 24px;
  font-size: 12px;
  color: #9aa0a6;
  text-align: center;
  line-height: 1.6;
}

.login-btn {
  width: 100%;
}
</style>
