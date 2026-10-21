/**
 * 一键添加：文本模板 + 自动解析
 * 规则：每行「字段：值」，支持中文/英文冒号；描述类字段支持多行续写
 */

export const HOUSE_TEMPLATE = `标题：地铁口精装一居室 拎包入住
月租：1800
押金：押一付一
户型：1室1厅
面积：48
楼层：6/18
朝向：朝南
装修：精装修
城市：示例市
小区：阳光小区
地址：阳光小区 3 栋 601
标签：近地铁,随时看房
配套：床,衣柜,空调,洗衣机,冰箱
联系人：王先生
电话：13000000000
微信：wang123
描述：房屋采光好，家电齐全，可拎包入住。
步行 5 分钟到地铁口。`

export const JOB_TEMPLATE = `职位：帮厨
店铺：王记小吃
类型：临时
薪资：150元/天
结算：日结
时段：早班 9:00-14:00
小区：阳光小区
地址：阳光小区东门旁
人数：2人
经验：不限
福利：管饭,包一餐
联系人：王老板
电话：13800138000
微信：wanglaoban
描述：负责配菜、洗碗、收台。
手脚麻利、住附近优先。`

const HOUSE_ALIAS = {
  title: ['标题', '房源标题', '名称', '房源名称'],
  price: ['月租', '租金', '价格', '月租金', '房租'],
  depositType: ['押金', '押金方式', '付款方式', '付租方式'],
  layout: ['户型', '房型', '几室'],
  area: ['面积', '建筑面积', '平米'],
  floor: ['楼层', '所在楼层'],
  totalFloor: ['总楼层', '共几层'],
  orientation: ['朝向'],
  decoration: ['装修', '装修情况'],
  city: ['城市'],
  district: ['小区', '区域', '商圈', '所在小区', '小区/区域'],
  address: ['地址', '详细地址', '房屋地址'],
  tags: ['标签', '特点', '亮点'],
  facilities: ['配套', '设施', '房屋配套', '家具家电'],
  contactName: ['联系人', '房东', '姓名', '称呼'],
  contactPhone: ['电话', '联系电话', '手机', '手机号', '联系方式'],
  contactWechat: ['微信', '微信号'],
  description: ['描述', '房源描述', '详情', '说明']
}

const JOB_ALIAS = {
  title: ['职位', '岗位', '职位名称', '招聘', '招什么'],
  companyName: ['店铺', '公司', '商家', '单位', '店名', '公司名称'],
  companyType: ['行业', '店铺类型', '性质', '公司性质'],
  jobType: ['类型', '工作类型', '用工类型'],
  salaryText: ['薪资', '工资', '待遇', '薪资文案', '报酬'],
  salaryMin: ['薪资下限', '最低工资'],
  salaryMax: ['薪资上限', '最高工资'],
  payType: ['结算', '结算方式', '怎么结'],
  workTime: ['时段', '工作时段', '班次', '时间'],
  city: ['城市'],
  district: ['小区', '区域', '商圈', '所在小区'],
  address: ['地址', '工作地址', '地点'],
  recruitCount: ['人数', '招聘人数', '招几人', '要几个人'],
  experience: ['经验', '经验要求', '要求经验'],
  education: ['学历', '学历要求'],
  welfare: ['福利', '待遇福利', '好处'],
  contactName: ['联系人', '老板', '姓名', '称呼'],
  contactPhone: ['电话', '联系电话', '手机', '手机号', '联系方式'],
  contactWechat: ['微信', '微信号'],
  description: ['描述', '职位描述', '要求', '详情', '说明', '工作内容']
}

const NUMBER_FIELDS_HOUSE = ['price', 'area']
const NUMBER_FIELDS_JOB = ['salaryMin', 'salaryMax']

function matchField(key, aliasMap) {
  const k = key.replace(/\s/g, '')
  for (const field of Object.keys(aliasMap)) {
    if (aliasMap[field].some((alias) => alias === k || k === alias)) {
      return field
    }
  }
  // 模糊匹配：包含关系
  for (const field of Object.keys(aliasMap)) {
    if (aliasMap[field].some((alias) => k.includes(alias) || alias.includes(k))) {
      return field
    }
  }
  return null
}

function parseText(text, aliasMap, numberFields) {
  const lines = String(text || '').split(/\r?\n/)
  const data = {}
  const unknown = []
  let descField = null

  lines.forEach((raw) => {
    const line = raw.trim()
    if (!line) return

    // 分隔线 / 纯符号行忽略
    if (/^[-=*#]{2,}$/.test(line)) return

    const m = line.match(/^([^:：]{1,12})\s*[:：]\s*(.*)$/)
    if (m) {
      const key = m[1].trim()
      const value = m[2].trim()
      const field = matchField(key, aliasMap)
      if (field) {
        data[field] = value
        descField = field === 'description' ? field : null
      } else {
        unknown.push(line)
        descField = null
      }
      return
    }

    // 无冒号的行：跟在描述后面则续写描述
    if (descField) {
      data[descField] = (data[descField] ? data[descField] + '\n' : '') + line
    } else {
      unknown.push(line)
    }
  })

  // 数字字段转换
  numberFields.forEach((f) => {
    if (data[f] !== undefined) {
      const n = Number(String(data[f]).replace(/[^\d.]/g, ''))
      data[f] = isNaN(n) ? 0 : n
    }
  })

  return { data, unknown }
}

/**
 * 解析房源文本
 */
export function parseHouse(text) {
  const { data, unknown } = parseText(text, HOUSE_ALIAS, NUMBER_FIELDS_HOUSE)

  // 楼层写法 6/18 → floor=6 totalFloor=18
  if (data.floor && String(data.floor).includes('/')) {
    const parts = String(data.floor).split('/')
    data.floor = parts[0].trim()
    if (!data.totalFloor && parts[1]) {
      data.totalFloor = parts[1].trim()
    }
  }

  // 户型写「1室1厅」时无需处理；面积补单位
  if (data.area) {
    data.area = Number(String(data.area).replace(/[^\d.]/g, '')) || 0
  }

  return { data, unknown, missing: ['title', 'price'].filter((f) => !data[f]) }
}

/**
 * 解析招聘文本
 */
export function parseJob(text) {
  const { data, unknown } = parseText(text, JOB_ALIAS, NUMBER_FIELDS_JOB)

  // 薪资文案写「6000-9000」时同步拆成上下限
  if (data.salaryText && !data.salaryMin && !data.salaryMax) {
    const m = String(data.salaryText).match(/(\d+)\s*[-~至]\s*(\d+)/)
    if (m) {
      data.salaryMin = Number(m[1])
      data.salaryMax = Number(m[2])
    }
  }

  return { data, unknown, missing: ['title', 'companyName'].filter((f) => !data[f]) }
}
