const request = require('../utils/request.js');

/**
 * 房源列表
 * @param {Object} params { pageNum, pageSize, keyword, district, priceRange, layout, sort }
 */
function list(params = {}) {
  return request.get('/api/open/house/list', params);
}

/** 房源详情 */
function detail(id) {
  return request.get('/api/open/house/' + id);
}

module.exports = { list, detail };
