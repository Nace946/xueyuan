<template>
  <el-dialog
    :model-value="visible"
    :title="id ? '编辑招聘' : '发布招聘'"
    width="min(900px, 94vw)"
    :close-on-click-modal="false"
    @update:model-value="(v) => emit('update:modelValue', v)"
    @closed="onClosed"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="职位名称" prop="title">
            <el-input v-model="form.title" placeholder="如：门店店长" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="公司名称" prop="companyName">
            <el-input v-model="form.companyName" placeholder="如：示例商贸有限公司" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="6">
          <el-form-item label="工作类型">
            <el-select v-model="form.jobType" style="width:100%">
              <el-option label="临时" value="临时" />
              <el-option label="兼职" value="兼职" />
              <el-option label="全职" value="全职" />
              <el-option label="实习" value="实习" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="薪资范围(元/月)" label-width="120px">
            <div class="salary-range">
              <el-input-number v-model="form.salaryMin" :min="0" :step="10" controls-position="right" class="salary-num" />
              <span class="salary-sep">至</span>
              <el-input-number v-model="form.salaryMax" :min="0" :step="10" controls-position="right" class="salary-num" />
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="薪资文案" label-width="80px">
            <el-input v-model="form.salaryText" placeholder="如 150元/天" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="6">
          <el-form-item label="结算方式">
            <el-select v-model="form.payType" placeholder="选填" clearable style="width:100%">
              <el-option label="日结" value="日结" />
              <el-option label="周结" value="周结" />
              <el-option label="月结" value="月结" />
              <el-option label="完工结" value="完工结" />
              <el-option label="面议" value="面议" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="工作时段">
            <el-input v-model="form.workTime" placeholder="如：早班 9:00-14:00" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="所在小区">
            <el-input v-model="form.district" placeholder="如：阳光小区 / 3 期" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="招聘人数">
            <el-input v-model="form.recruitCount" placeholder="2人" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="6">
          <el-form-item label="公司性质">
            <el-input v-model="form.companyType" placeholder="超市 / 小吃店 / 摊位" />
          </el-form-item>
        </el-col>
        <el-col :span="18">
          <el-form-item label="工作地址">
            <el-input v-model="form.address" placeholder="如：xx小区东门旁王记小吃" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="6">
          <el-form-item label="经验要求">
            <el-input v-model="form.experience" placeholder="选填，如：不限" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="学历要求">
            <el-input v-model="form.education" placeholder="选填，临工一般不限" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="工作城市">
            <el-input v-model="form.city" placeholder="选填，社区场景可不填" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="公司LOGO">
        <el-upload
          class="logo-uploader"
          :action="uploadAction"
          name="file"
          :headers="headers"
          :show-file-list="false"
          accept="image/*"
          :on-success="onLogoSuccess"
        >
          <img v-if="form.companyLogo" :src="form.companyLogo" class="logo" />
          <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
        </el-upload>
        <div class="form-tip">建议 200x200 正方形图片，不上传时小程序端显示默认占位图</div>
      </el-form-item>

      <el-form-item label="职位福利">
        <el-input v-model="form.welfare" placeholder="多个用英文逗号分隔，如：五险一金,包吃住,绩效奖金" />
      </el-form-item>

      <el-form-item label="标签">
        <el-input v-model="form.tags" placeholder="如：急招,日结" />
      </el-form-item>

      <el-form-item label="职位描述">
        <el-input v-model="form.description" type="textarea" :rows="6" placeholder="建议分「岗位职责」「任职要求」两段，换行会在小程序端分段展示" />
      </el-form-item>

      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="联系人">
            <el-input v-model="form.contactName" placeholder="张经理" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="联系电话">
            <el-input v-model="form.contactPhone" placeholder="13800138000" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="微信号">
            <el-input v-model="form.contactWechat" placeholder="选填" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="6">
          <el-form-item label="排序值">
            <el-input-number v-model="form.sort" :min="0" :step="1" controls-position="right" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="首页推荐">
            <el-switch v-model="form.recommend" :active-value="1" :inactive-value="0" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="上架状态">
            <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="上架" inactive-text="下架" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <el-button @click="emit('update:modelValue', false)">取消</el-button>
      <el-button type="primary" :loading="saving" @click="onSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import * as api from '../../api/job'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  id: { type: [Number, String], default: null }
})
const emit = defineEmits(['update:modelValue', 'saved'])

const visible = ref(props.modelValue)
watch(() => props.modelValue, (v) => { visible.value = v })
watch(visible, (v) => { if (v !== props.modelValue) emit('update:modelValue', v) })

const uploadAction = '/api/admin/upload/image'
const headers = { Authorization: 'Bearer ' + (localStorage.getItem('admin_token') || '') }

const formRef = ref()
const saving = ref(false)

function emptyForm() {
  return {
    title: '',
    companyName: '',
    companyLogo: '',
    companyType: '',
    jobType: '临时',
    salaryMin: 0,
    salaryMax: 0,
    salaryText: '',
    payType: '日结',
    city: '',
    district: '',
    address: '',
    workTime: '',
    experience: '',
    education: '',
    recruitCount: '',
    welfare: '',
    tags: '',
    description: '',
    contactName: '',
    contactPhone: '',
    contactWechat: '',
    sort: 0,
    recommend: 0,
    status: 1
  }
}

const form = ref(emptyForm())
const rules = {
  title: [{ required: true, message: '请输入职位名称', trigger: 'blur' }],
  companyName: [{ required: true, message: '请输入公司名称', trigger: 'blur' }]
}

watch(
  () => [props.modelValue, props.id],
  async ([v, id]) => {
    if (!v) return
    if (id) {
      const data = await api.get(id)
      form.value = Object.assign(emptyForm(), data || {})
    } else {
      form.value = emptyForm()
    }
  },
  { immediate: true }
)

function onClosed() {
  form.value = emptyForm()
}

/**
 * 一键添加：外部解析出的数据直接灌入表单
 */
function fillForm(data) {
  form.value = Object.assign(emptyForm(), data || {})
}

defineExpose({ fillForm })

function onLogoSuccess(res) {
  if (res && res.code === 0 && res.data && res.data.url) {
    form.value.companyLogo = res.data.url
    ElMessage.success('上传成功')
  } else {
    ElMessage.error((res && res.msg) || '上传失败')
  }
}

async function onSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    const payload = Object.assign({}, form.value)
    if (props.id) {
      await api.update(props.id, payload)
    } else {
      await api.save(payload)
    }
    ElMessage.success('保存成功')
    emit('update:modelValue', false)
    emit('saved')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.salary-range {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.salary-num {
  flex: 1;
  min-width: 0;
}

.salary-sep {
  color: #9aa0a6;
  flex-shrink: 0;
}

.logo-uploader .logo {
  width: 90px;
  height: 90px;
  border-radius: 8px;
  display: block;
}

.logo-uploader-icon {
  width: 90px;
  height: 90px;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #8c939d;
}
</style>
