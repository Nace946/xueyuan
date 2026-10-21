const request = require('../utils/request.js');

/** 首页聚合数据 */
function homeIndex() {
  return request.get('/api/open/home/index');
}

module.exports = { homeIndex };
