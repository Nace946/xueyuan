// app.js
const { baseUrl } = require('./utils/config.js');

App({
  globalData: {
    baseUrl,
    systemInfo: null
  },

  onLaunch() {
    // 读取设备信息，用于自定义导航栏与安全区适配
    try {
      const info = wx.getSystemInfoSync();
      this.globalData.systemInfo = info;
    } catch (e) {
      console.warn('获取系统信息失败', e);
    }

    // 隐私协议：若小程序后台配置了用户隐私保护指引，需在此处理授权
    // 官方文档路径：微信公众平台 -> 设置 -> 服务内容声明 -> 用户隐私保护指引
    // 接口文档：https://developers.weixin.qq.com/miniprogram/dev/framework/user-privacy/PrivacyAuthorize.html
    if (wx.getPrivacySetting) {
      wx.getPrivacySetting({
        success: (res) => {
          // res.needAuthorization 为 true 时表示需要用户同意隐私指引
          this.globalData.needPrivacy = !!res.needAuthorization;
        },
        fail: () => {}
      });
    }
  }
});
