const request = require('../utils/request.js');

/**
 * 招聘列表
 * @param {Object} params { pageNum, pageSize, keyword, district, jobType, sort }
 */
function list(params = {}) {
  return request.get('/api/open/job/list', params);
}

/** 招聘详情 */
function detail(id) {
  return request.get('/api/open/job/' + id);
}

module.exports = { list, detail };
