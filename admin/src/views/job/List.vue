<template>
  <div class="page-container">
    <div class="card-panel">
      <div class="toolbar">
        <el-input v-model="query.keyword" placeholder="职位 / 公司 / 区域" clearable style="width:220px" @keyup.enter="load" />
        <el-select v-model="query.status" placeholder="状态" clearable style="width:120px">
          <el-option label="已上架" :value="1" />
          <el-option label="已下架" :value="0" />
        </el-select>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <div class="spacer"></div>
        <el-button @click="onCreate">发布招聘</el-button>
        <el-button type="success" @click="pasteVisible = true">一键添加</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="职位名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="companyName" label="公司" min-width="160" show-overflow-tooltip />
        <el-table-column label="薪资" width="140">
          <template #default="{ row }">
            <span v-if="row.salaryText">{{ row.salaryText }}</span>
            <span v-else>{{ row.salaryMin }} - {{ row.salaryMax }} 元/月</span>
          </template>
        </el-table-column>
        <el-table-column prop="jobType" label="类型" width="80" />
        <el-table-column label="结算" width="80">
          <template #default="{ row }">
            <span v-if="row.payType">{{ row.payType }}</span>
            <span v-else style="color:#9aa0a6">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="district" label="小区/区域" width="120" show-overflow-tooltip />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '已上架' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="onEdit(row)">编辑</el-button>
            <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="onToggle(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button link type="danger" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        style="margin-top:16px;justify-content:flex-end"
        background
        layout="total, sizes, prev, pager, next"
        :total="total"
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :page-sizes="[10, 20, 50]"
        @change="load"
      />
    </div>

    <JobEdit ref="editRef" v-model="editVisible" :id="editId" @saved="load" />

    <PasteAdd
      v-model="pasteVisible"
      title="一键添加招聘"
      :template="JOB_TEMPLATE"
      :parser="parseJob"
      :field-labels="JOB_LABELS"
      @filled="onPasteFilled"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as api from '../../api/job'
import JobEdit from './Edit.vue'
import PasteAdd from '../../components/PasteAdd.vue'
import { JOB_TEMPLATE, parseJob } from '../../utils/parser'

const JOB_LABELS = {
  title: '职位',
  companyName: '店铺/公司',
  companyType: '行业',
  jobType: '类型',
  salaryText: '薪资',
  salaryMin: '薪资下限',
  salaryMax: '薪资上限',
  payType: '结算方式',
  workTime: '工作时段',
  city: '城市',
  district: '小区',
  address: '地址',
  recruitCount: '人数',
  experience: '经验',
  education: '学历',
  welfare: '福利',
  contactName: '联系人',
  contactPhone: '电话',
  contactWechat: '微信',
  description: '描述'
}

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = ref({ pageNum: 1, pageSize: 10, keyword: '', status: null })

const editVisible = ref(false)
const editId = ref(null)
const editRef = ref()
const pasteVisible = ref(false)

async function load() {
  loading.value = true
  try {
    const data = await api.page(query.value)
    list.value = data.list || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function reset() {
  query.value = { pageNum: 1, pageSize: 10, keyword: '', status: null }
  load()
}

function onCreate() {
  editId.value = null
  editVisible.value = true
}

function onEdit(row) {
  editId.value = row.id
  editVisible.value = true
}

/**
 * 一键添加：解析结果带入编辑表单，用户确认后再保存
 */
function onPasteFilled(data) {
  editId.value = null
  editVisible.value = true
  nextTick(() => {
    if (editRef.value && editRef.value.fillForm) {
      editRef.value.fillForm(data)
    }
  })
}

async function onToggle(row) {
  await api.updateStatus(row.id, row.status === 1 ? 0 : 1)
  ElMessage.success('操作成功')
  load()
}

function onDelete(row) {
  ElMessageBox.confirm('确认删除该职位？删除后小程序端将无法访问。', '提示', { type: 'warning' })
    .then(async () => {
      await api.remove(row.id)
      ElMessage.success('删除成功')
      load()
    })
    .catch(() => {})
}

onMounted(load)
</script>
