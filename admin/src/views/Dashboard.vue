<template>
  <div class="page-container">
    <el-row :gutter="16">
      <el-col :span="6">
        <div class="stat-card">
          <div>
            <div class="stat-value">{{ stats.houseCount }}</div>
            <div class="stat-label">在租房源</div>
          </div>
          <el-icon :size="36" color="#2b7de9"><House /></el-icon>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div>
            <div class="stat-value">{{ stats.jobCount }}</div>
            <div class="stat-label">在招职位</div>
          </div>
          <el-icon :size="36" color="#ff6b35"><Briefcase /></el-icon>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div>
            <div class="stat-value">{{ stats.bannerCount }}</div>
            <div class="stat-label">首页轮播</div>
          </div>
          <el-icon :size="36" color="#22b573"><Picture /></el-icon>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div>
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">信息总量</div>
          </div>
          <el-icon :size="36" color="#8b5cf6"><DataLine /></el-icon>
        </div>
      </el-col>
    </el-row>

    <div class="card-panel" style="margin-top:16px">
      <div style="font-size:16px;font-weight:600;margin-bottom:12px">快捷操作</div>
      <el-button type="primary" @click="$router.push('/house')">发布房源</el-button>
      <el-button type="warning" @click="$router.push('/job')">发布招聘</el-button>
      <el-button @click="$router.push('/banner')">首页轮播配置</el-button>

      <div style="margin-top:20px" class="form-tip">
        使用说明：本后台仅负责信息发布与上下架管理；小程序端为纯展示，无需用户登录。<br />
        发布的信息请自行核实真实性，租房/招聘类信息涉及用户权益，务必避免虚假宣传。
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { House, Briefcase, Picture, DataLine } from '@element-plus/icons-vue'

const stats = ref({ houseCount: 0, jobCount: 0, bannerCount: 0, total: 0 })

onMounted(async () => {
  try {
    const { data } = await axios.get('/api/open/home/index')
    if (data && data.code === 0) {
      const s = data.data.stats || {}
      const banners = data.data.banners || []
      stats.value = {
        houseCount: s.houseCount || 0,
        jobCount: s.jobCount || 0,
        bannerCount: banners.length,
        total: (s.houseCount || 0) + (s.jobCount || 0)
      }
    }
  } catch (e) {
    // 概览数据获取失败不阻塞页面
  }
})
</script>
