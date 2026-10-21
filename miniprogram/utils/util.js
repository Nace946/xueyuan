/**
 * 通用工具函数
 */
const { baseUrl } = require('./config.js');

/**
 * 相对路径补全为绝对地址
 * 后端上传接口返回的是相对路径（如 /uploads/2026/10/18/a.jpg），
 * 小程序的 image src 不能直接用相对路径，必须拼上接口域名。
 */
function absUrl(url) {
  if (!url) return '';
  if (/^https?:\/\//i.test(url)) return url;     // 已是完整地址
  if (url.charAt(0) === '/') return baseUrl + url; // 以 / 开头 → 拼接接口域名
  return url;                                     // 其它（如本地占位路径）原样返回
}

function absUrls(list) {
  return (list || []).map(absUrl);
}

// 格式化价格：后端返回单位为「元」的数值
function formatPrice(value) {
  if (value === null || value === undefined || value === '') return '面议';
  const n = Number(value);
  if (isNaN(n)) return String(value);
  return n % 1 === 0 ? String(n) : n.toFixed(2);
}

// 相对时间
function fromNow(timeStr) {
  if (!timeStr) return '';
  const t = new Date(String(timeStr).replace(/-/g, '/'));
  if (isNaN(t.getTime())) return String(timeStr).slice(0, 10);
  const diff = Date.now() - t.getTime();
  const day = 24 * 60 * 60 * 1000;
  if (diff < 60 * 1000) return '刚刚';
  if (diff < 60 * 60 * 1000) return Math.floor(diff / 60000) + '分钟前';
  if (diff < day) return Math.floor(diff / 3600000) + '小时前';
  if (diff < 3 * day) return Math.floor(diff / day) + '天前';
  return String(timeStr).slice(0, 10);
}

// 复制文本
function copyText(text) {
  wx.setClipboardData({
    data: text,
    success() {
      wx.showToast({ title: '已复制', icon: 'none' });
    }
  });
}

// 拨打电话
function callPhone(phone) {
  if (!phone) {
    wx.showToast({ title: '暂无联系方式', icon: 'none' });
    return;
  }
  wx.makePhoneCall({
    phoneNumber: phone,
    fail() {}
  });
}

// 预览图片
function previewImage(current, urls) {
  if (!urls || !urls.length) return;
  wx.previewImage({ current, urls });
}

// 解析后端数组字段：兼容 JSON 数组字符串与逗号分隔字符串
function parseArray(value) {
  if (!value) return [];
  if (Array.isArray(value)) return value;
  try {
    const arr = JSON.parse(value);
    if (Array.isArray(arr)) return arr;
  } catch (e) {
    // 非 JSON 时按逗号分隔兜底
  }
  return String(value)
    .split(/[,，、]/)
    .map((s) => s.trim())
    .filter((s) => s);
}

module.exports = {
  absUrl,
  absUrls,
  formatPrice,
  fromNow,
  copyText,
  callPhone,
  previewImage,
  parseArray
};
