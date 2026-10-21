const { baseUrl } = require('./config.js');

/**
 * 统一请求封装
 * 后端统一返回结构：{ code: 0, msg: 'ok', data: {...} }
 */
function request(options) {
  const { url, method = 'GET', data = {}, header = {}, loading = false } = options;

  if (loading) {
    wx.showLoading({ title: '加载中', mask: true });
  }

  return new Promise((resolve, reject) => {
    wx.request({
      url: baseUrl + url,
      method,
      data,
      header: {
        'content-type': 'application/json',
        ...header
      },
      timeout: 15000,
      success(res) {
        if (loading) wx.hideLoading();
        const statusCode = res.statusCode;
        if (statusCode >= 200 && statusCode < 300) {
          const body = res.data || {};
          if (body.code === 0) {
            resolve(body.data);
          } else {
            // 业务异常：统一提示，仍 reject 便于页面做兜底
            if (body.msg) {
              wx.showToast({ title: body.msg, icon: 'none' });
            }
            reject(new Error(body.msg || '请求失败'));
          }
        } else {
          wx.showToast({ title: '网络异常，请稍后重试', icon: 'none' });
          reject(new Error('HTTP ' + statusCode));
        }
      },
      fail(err) {
        if (loading) wx.hideLoading();
        wx.showToast({ title: '网络连接失败', icon: 'none' });
        reject(err);
      }
    });
  });
}

module.exports = {
  get: (url, data, options = {}) => request({ url, method: 'GET', data, ...options }),
  post: (url, data, options = {}) => request({ url, method: 'POST', data, ...options }),
  request
};
