const util = require('./util.js');

/**
 * 房源数据格式化：补充展示字段，避免在 WXML 里写复杂表达式
 */
function formatHouse(item) {
  if (!item) return item;
  const price = Number(item.price || 0);
  item.priceText = price > 0 ? util.formatPrice(price) : '面议';

  let floorText = '';
  if (item.floor) floorText = item.floor + '层';
  if (item.totalFloor) floorText += '/共' + item.totalFloor + '层';
  item.floorText = floorText;

  item.tagList = util.parseArray(item.tags);
  item.facilityList = util.parseArray(item.facilities);
  item.imageList = util.parseArray(item.images).map(util.absUrl);
  if (!item.cover && item.imageList.length) item.cover = item.imageList[0];
  // 后端返回的是相对路径，补全域名后才能被 image 组件加载
  item.cover = util.absUrl(item.cover);
  item.timeText = util.fromNow(item.createTime);
  return item;
}

/**
 * 招聘数据格式化
 */
function formatJob(item) {
  if (!item) return item;
  const min = Number(item.salaryMin || 0);
  const max = Number(item.salaryMax || 0);
  if (item.salaryText) {
    // 后台已手填薪资文案时优先使用
  } else if (min > 0 && max > 0) {
    item.salaryText = min + '-' + max + '元/月';
  } else if (min > 0) {
    item.salaryText = min + '元/月起';
  } else {
    item.salaryText = '面议';
  }

  item.tagList = util.parseArray(item.tags);
  item.welfareList = util.parseArray(item.welfare);
  item.companyLogo = util.absUrl(item.companyLogo);
  item.timeText = util.fromNow(item.createTime);
  return item;
}

function formatHouseList(list) {
  return (list || []).map(formatHouse);
}

function formatJobList(list) {
  return (list || []).map(formatJob);
}

module.exports = {
  formatHouse,
  formatJob,
  formatHouseList,
  formatJobList
};
