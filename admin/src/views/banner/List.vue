<template>
  <div class="page-container">
    <div class="card-panel">
      <div class="toolbar">
        <span style="font-size:16px;font-weight:600">首页轮播</span>
        <div class="spacer"></div>
        <el-button type="primary" @click="onCreate">新增轮播</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="图片" width="180">
          <template #default="{ row }">
            <el-image
              v-if="row.image"
              :src="row.image"
              :preview-src-list="[row.image]"
              fit="cover"
              style="width:150px;height:60px;border-radius:4px"
            />
            <span v-else style="color:#9aa0a6">无图</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
        <el-table-column label="跳转类型" width="110">
          <template #default="{ row }">
            {{ linkTypeText(row.linkType) }}
          </template>
        </el-table-column>
        <el-table-column label="跳转目标" min-width="180">
          <template #default="{ row }">
            <span v-if="row.linkType === 'house'">房源 ID：{{ row.linkId }}</span>
            <span v-else-if="row.linkType === 'job'">职位 ID：{{ row.linkId }}</span>
            <span v-else-if="row.linkType === 'url'">{{ row.linkUrl }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="onEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑轮播' : '新增轮播'" width="600px" :close-on-click-modal="false">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="选填，仅后台备注用" />
        </el-form-item>
        <el-form-item label="轮播图片">
          <el-upload
            class="banner-uploader"
            :action="uploadAction"
            name="file"
            :headers="headers"
            :show-file-list="false"
            accept="image/*"
            :on-success="onImageSuccess"
          >
            <img v-if="form.image" :src="form.image" class="banner-img" />
            <el-icon v-else class="banner-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="form-tip">建议尺寸 750x420，比例 16:9 左右</div>
        </el-form-item>
        <el-form-item label="跳转类型">
          <el-select v-model="form.linkType" style="width:100%">
            <el-option label="不跳转" value="none" />
            <el-option label="房源详情" value="house" />
            <el-option label="职位详情" value="job" />
            <el-option label="外部链接" value="url" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.linkType === 'house' || form.linkType === 'job'" label="目标ID">
          <el-input-number v-model="form.linkId" :min="1" :step="1" controls-position="right" style="width:100%" />
        </el-form-item>
        <el-form-item v-if="form.linkType === 'url'" label="链接地址">
          <el-input v-model="form.linkUrl" placeholder="https:// （需在小程序后台配置为业务域名）" />
        </el-form-item>
        <el-form-item label="排序值">
          <el-input-number v-model="form.sort" :min="0" :step="1" controls-position="right" style="width:100%" />
        </el-form-item>
        <el-form-item label="启用状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import * as api from '../../api/banner'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)

const uploadAction = '/api/admin/upload/image'
const headers = { Authorization: 'Bearer ' + (localStorage.getItem('admin_token') || '') }

function emptyForm() {
  return {
    id: null,
    title: '',
    image: '',
    linkType: 'none',
    linkId: null,
    linkUrl: '',
    sort: 0,
    status: 1
  }
}

const form = ref(emptyForm())

function linkTypeText(type) {
  const map = { none: '不跳转', house: '房源详情', job: '职位详情', url: '外部链接' }
  return map[type] || type
}

async function load() {
  loading.value = true
  try {
    list.value = (await api.list()) || []
  } finally {
    loading.value = false
  }
}

function onCreate() {
  form.value = emptyForm()
  dialogVisible.value = true
}

function onEdit(row) {
  form.value = Object.assign(emptyForm(), row)
  dialogVisible.value = true
}

function onImageSuccess(res) {
  if (res && res.code === 0 && res.data && res.data.url) {
    form.value.image = res.data.url
    ElMessage.success('上传成功')
  } else {
    ElMessage.error((res && res.msg) || '上传失败')
  }
}

async function onSubmit() {
  if (!form.value.image) {
    ElMessage.warning('请上传轮播图片')
    return
  }
  saving.value = true
  try {
    const payload = Object.assign({}, form.value)
    if (payload.id) {
      await api.update(payload.id, payload)
    } else {
      await api.save(payload)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

function onDelete(row) {
  ElMessageBox.confirm('确认删除该轮播？', '提示', { type: 'warning' })
    .then(async () => {
      await api.remove(row.id)
      ElMessage.success('删除成功')
      load()
    })
    .catch(() => {})
}

onMounted(load)
</script>

<style scoped>
.banner-uploader .banner-img {
  width: 260px;
  height: 110px;
  border-radius: 8px;
  display: block;
  object-fit: cover;
}

.banner-uploader-icon {
  width: 260px;
  height: 110px;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #8c939d;
}
</style>
