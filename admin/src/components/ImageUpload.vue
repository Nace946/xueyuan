<template>
  <el-upload
    v-model:file-list="fileList"
    :action="uploadAction"
    name="file"
    :headers="headers"
    list-type="picture-card"
    accept="image/*"
    :limit="limit"
    :on-success="onSuccess"
    :on-remove="onChange"
    :on-exceed="onExceed"
  >
    <el-icon><Plus /></el-icon>
    <template #tip>
      <div class="el-upload__tip">建议尺寸 800x600，单张不超过 10MB，第一张作为封面</div>
    </template>
  </el-upload>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const props = defineProps({
  // JSON 数组字符串，例如 ["https://xxx/a.jpg"]
  modelValue: { type: String, default: '[]' },
  limit: { type: Number, default: 9 }
})
const emit = defineEmits(['update:modelValue'])

const uploadAction = '/api/admin/upload/image'
const headers = { Authorization: 'Bearer ' + (localStorage.getItem('admin_token') || '') }

const fileList = ref([])

function toArray(value) {
  if (!value) return []
  if (Array.isArray(value)) return value
  try {
    const arr = JSON.parse(value)
    return Array.isArray(arr) ? arr : []
  } catch (e) {
    return []
  }
}

watch(
  () => props.modelValue,
  (val) => {
    const arr = toArray(val)
    fileList.value = arr.map((url, index) => ({ name: '图片' + (index + 1), url }))
  },
  { immediate: true }
)

function emitValue() {
  emit('update:modelValue', JSON.stringify(fileList.value.map((f) => f.url)))
}

function onSuccess(res, file, files) {
  if (res && res.code === 0 && res.data && res.data.url) {
    // 图片地址由后端返回，可能是相对路径 /uploads/xxx.jpg
    const target = files.find((f) => f === file)
    if (target) target.url = res.data.url
    emitValue()
  } else {
    ElMessage.error((res && res.msg) || '上传失败')
  }
}

function onChange() {
  emitValue()
}

function onExceed() {
  ElMessage.warning('最多上传 ' + props.limit + ' 张图片')
}
</script>
