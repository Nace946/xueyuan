// pages/detail/webview/webview.js
// 说明：web-view 组件仅支持非个人主体小程序，且目标域名需在小程序后台配置为业务域名。
// 官方文档：https://developers.weixin.qq.com/miniprogram/dev/component/web-view.html
const util = require('../../../utils/util.js');

Page({
  data: {
    url: ''
  },
  onLoad(options) {
    if (!options.url) {
      wx.showToast({ title: '链接缺失', icon: 'none' });
      return;
    }
    const url = decodeURIComponent(options.url);
    this.setData({ url });
  },
  onShareAppMessage() {
    return { title: '租房招聘通', path: '/pages/index/index' };
  },
  onCopyLink() {
    util.copyText(this.data.url);
  }
});
