// utils/config.js —— 环境配置
// 上线前把 baseUrl 改成已备案且在小程序后台配置过的 https 域名
module.exports = {
  // 开发环境：勾选开发者工具「不校验合法域名」后可用 http://localhost
  baseUrl: 'http://localhost:8080',
  // 生产环境示例（需备案 + 小程序后台 request 合法域名配置）：
  // baseUrl: 'https://api.example.com',
  pageSize: 10
};
