<template>
  <el-dialog
    :model-value="visible"
    :title="id ? '编辑房源' : '发布房源'"
    width="min(900px, 94vw)"
    :close-on-click-modal="false"
    @update:model-value="(v) => emit('update:modelValue', v)"
    @closed="onClosed"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="房源标题" prop="title">
            <el-input v-model="form.title" placeholder="如：地铁口精装一居室 拎包入住" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="月租(元)" prop="price">
            <el-input-number v-model="form.price" :min="0" :step="100" controls-position="right" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="押金方式">
            <el-select v-model="form.depositType" placeholder="请选择" clearable style="width:100%">
              <el-option label="押一付一" value="押一付一" />
              <el-option label="押一付三" value="押一付三" />
              <el-option label="押二付一" value="押二付一" />
              <el-option label="面议" value="面议" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="6">
          <el-form-item label="户型">
            <el-input v-model="form.layout" placeholder="1室1厅" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="面积(㎡)">
            <el-input-number v-model="form.area" :min="0" :step="1" controls-position="right" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="所在楼层">
            <el-input v-model="form.floor" placeholder="6" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="总楼层">
            <el-input v-model="form.totalFloor" placeholder="18" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="6">
          <el-form-item label="朝向">
            <el-input v-model="form.orientation" placeholder="朝南" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="装修">
            <el-select v-model="form.decoration" placeholder="请选择" clearable style="width:100%">
              <el-option label="精装修" value="精装修" />
              <el-option label="简装修" value="简装修" />
              <el-option label="毛坯" value="毛坯" />
              <el-option label="豪装" value="豪装" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="城市">
            <el-input v-model="form.city" placeholder="如：深圳市" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="区域/商圈">
            <el-input v-model="form.district" placeholder="如：南山区" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="详细地址">
        <el-input v-model="form.address" placeholder="如：xx路88号花园小区3栋601" />
      </el-form-item>

      <el-form-item label="房源图片">
        <ImageUpload v-model="form.images" :limit="9" />
      </el-form-item>

      <el-form-item label="标签">
        <el-input v-model="form.tags" placeholder="多个标签用英文逗号分隔，如：近地铁,随时看房" />
      </el-form-item>

      <el-form-item label="配套设施">
        <el-input v-model="form.facilities" placeholder="如：床,衣柜,空调,洗衣机,冰箱,宽带" />
      </el-form-item>

      <el-form-item label="房源描述">
        <el-input v-model="form.description" type="textarea" :rows="5" placeholder="换行会在小程序端分段展示" />
      </el-form-item>

      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="联系人">
            <el-input v-model="form.contactName" placeholder="王先生" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="联系电话">
            <el-input v-model="form.contactPhone" placeholder="13800138000" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="微信号">
            <el-input v-model="form.contactWechat" placeholder="选填，用户可一键复制" />
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
import * as api from '../../api/house'
import ImageUpload from '../../components/ImageUpload.vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  id: { type: [Number, String], default: null }
})
const emit = defineEmits(['update:modelValue', 'saved'])

const visible = ref(props.modelValue)
watch(() => props.modelValue, (v) => { visible.value = v })
watch(visible, (v) => { if (v !== props.modelValue) emit('update:modelValue', v) })

const formRef = ref()
const saving = ref(false)

function emptyForm() {
  return {
    title: '',
    price: 0,
    depositType: '',
    layout: '',
    area: 0,
    floor: '',
    totalFloor: '',
    orientation: '',
    decoration: '',
    city: '',
    district: '',
    address: '',
    images: '[]',
    tags: '',
    facilities: '',
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
  title: [{ required: true, message: '请输入房源标题', trigger: 'blur' }],
  price: [{ required: true, message: '请输入月租价格', trigger: 'blur' }]
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
 * 一键添加：外部解析出的数据直接灌入表单（未填字段保持默认值）
 */
function fillForm(data) {
  form.value = Object.assign(emptyForm(), data || {})
  if (!form.value.images) {
    form.value.images = '[]'
  }
}

defineExpose({ fillForm })

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
