<template>
  <el-dialog
    :model-value="visible"
    :title="title"
    width="min(780px, 94vw)"
    :close-on-click-modal="false"
    @update:model-value="(v) => emit('update:modelValue', v)"
    @closed="reset"
  >
    <el-alert
      type="info"
      :closable="false"
      show-icon
      title="复制下方模板 → 按模板填好信息 → 粘贴到输入框 → 点「解析并填充」，解析结果会带入表单，确认无误后保存"
      style="margin-bottom: 14px"
    />

    <div class="template-box">
      <div class="template-head">
        <span class="template-title">信息模板</span>
        <el-button size="small" type="primary" link @click="copyTemplate">复制模板</el-button>
      </div>
      <pre class="template-text">{{ template }}</pre>
    </div>

    <el-input
      v-model="text"
      type="textarea"
      :rows="9"
      placeholder="把填写好的信息粘贴到这里（每行一个字段，格式：字段名：值）"
    />

    <div v-if="parsed" class="result">
      <el-alert
        v-if="missing.length"
        type="warning"
        :closable="false"
        show-icon
        :title="'缺少必填项：' + missing.map((f) => fieldLabels[f] || f).join('、') + '，可在填充后手动补'"
        style="margin-bottom: 10px"
      />
      <el-alert
        v-if="unknown.length"
        type="warning"
        :closable="false"
        show-icon
        :title="'有 ' + unknown.length + ' 行未识别：' + unknown.join(' | ')"
        style="margin-bottom: 10px"
      />
      <el-table :data="preview" size="small" border max-height="220">
        <el-table-column prop="label" label="字段" width="130" />
        <el-table-column prop="value" label="解析结果" min-width="200" show-overflow-tooltip />
      </el-table>
    </div>

    <template #footer>
      <el-button @click="emit('update:modelValue', false)">取消</el-button>
      <el-button :disabled="!text.trim()" @click="onParse">解析</el-button>
      <el-button type="primary" :disabled="!parsed" @click="onConfirm">填充到表单</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  title: { type: String, default: '一键添加' },
  template: { type: String, default: '' },
  parser: { type: Function, required: true },
  fieldLabels: { type: Object, default: () => ({}) }
})
const emit = defineEmits(['update:modelValue', 'filled'])

const visible = ref(props.modelValue)
watch(() => props.modelValue, (v) => { visible.value = v })
watch(visible, (v) => { if (v !== props.modelValue) emit('update:modelValue', v) })

const text = ref('')
const parsed = ref(false)
const preview = ref([])
const unknown = ref([])
const missing = ref([])
let parsedData = {}

function reset() {
  text.value = ''
  parsed.value = false
  preview.value = []
  unknown.value = []
  missing.value = []
  parsedData = {}
}

function copyTemplate() {
  const ta = document.createElement('textarea')
  ta.value = props.template
  ta.style.position = 'fixed'
  ta.style.opacity = '0'
  document.body.appendChild(ta)
  ta.select()
  try {
    document.execCommand('copy')
    ElMessage.success('模板已复制，粘贴到微信/记事本填写即可')
  } catch (e) {
    ElMessage.warning('复制失败，请手动选中模板复制')
  }
  document.body.removeChild(ta)
}

function onParse() {
  const res = props.parser(text.value) || {}
  parsedData = res.data || {}
  unknown.value = res.unknown || []
  missing.value = res.missing || []
  preview.value = Object.keys(parsedData).map((key) => ({
    label: props.fieldLabels[key] || key,
    value: String(parsedData[key])
  }))
  parsed.value = true
  if (!preview.value.length) {
    ElMessage.warning('没有解析出任何字段，请检查是否使用了「字段名：值」的格式')
  } else {
    ElMessage.success('解析完成，共识别 ' + preview.value.length + ' 个字段')
  }
}

function onConfirm() {
  emit('filled', Object.assign({}, parsedData))
  emit('update:modelValue', false)
}
</script>

<style scoped>
.template-box {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  margin-bottom: 14px;
  overflow: hidden;
}

.template-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 12px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.template-title {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
}

.template-text {
  margin: 0;
  padding: 12px;
  font-size: 12px;
  line-height: 1.8;
  color: #4b5563;
  background: #ffffff;
  white-space: pre-wrap;
  font-family: Consolas, Menlo, monospace;
}

.result {
  margin-top: 14px;
}
</style>
