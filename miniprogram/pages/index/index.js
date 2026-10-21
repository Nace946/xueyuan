const homeService = require('../../services/home.js');
const { formatHouseList, formatJobList } = require('../../utils/format.js');
const util = require('../../utils/util.js');

Page({
  data: {
    banners: [],
    houses: [],
    jobs: [],
    loading: true,
    stats: { houseCount: 0, jobCount: 0 },
    // 更多便民服务入口（当前为占位模块，后续可逐个接入真实页面）
    services: [
      { key: 'second', name: '二手物品', icon: '/assets/icons/service-second.png' },
      { key: 'lost', name: '失物招领', icon: '/assets/icons/service-lost.png' },
      { key: 'carpool', name: '拼车互助', icon: '/assets/icons/service-carpool.png' },
      { key: 'consult', name: '本地咨询', icon: '/assets/icons/service-consult.png' }
    ]
  },

  onLoad() {
    this.loadHome();
  },

  onPullDownRefresh() {
    this.loadHome(() => wx.stopPullDownRefresh());
  },

  async loadHome(done) {
    this.setData({ loading: true });
    try {
      const data = await homeService.homeIndex();
      // 轮播图片补全域名
      const banners = (data.banners || []).map((b) => Object.assign({}, b, {
        image: util.absUrl(b.image)
      }));
      this.setData({
        banners,
        houses: formatHouseList(data.houses),
        jobs: formatJobList(data.jobs),
        stats: data.stats || { houseCount: 0, jobCount: 0 },
        loading: false
      });
    } catch (e) {
      this.setData({ loading: false });
    } finally {
      if (done) done();
    }
  },

  onBannerTap(e) {
    const item = e.currentTarget.dataset.item;
    if (!item) return;
    if (item.linkType === 'house' && item.linkId) {
      wx.navigateTo({ url: '/pages/detail/house/house?id=' + item.linkId });
    } else if (item.linkType === 'job' && item.linkId) {
      wx.navigateTo({ url: '/pages/detail/job/job?id=' + item.linkId });
    } else if (item.linkType === 'url' && item.linkUrl) {
      wx.navigateTo({
        url: '/pages/detail/webview/webview?url=' + encodeURIComponent(item.linkUrl)
      });
    }
  },

  goHouseList() {
    wx.switchTab({ url: '/pages/house/list/list' });
  },

  goJobList() {
    wx.switchTab({ url: '/pages/job/list/list' });
  },

  /**
   * 便民服务入口：二手物品 / 失物招领 / 拼车互助 / 本地咨询
   * 后续接入时把对应 key 改为 wx.navigateTo 跳转到真实页面即可
   */
  onServiceTap(e) {
    const name = e.currentTarget.dataset.name;
    wx.showToast({
      title: '「' + name + '」模块建设中，敬请期待',
      icon: 'none'
    });
  },

  onShareAppMessage() {
    return {
      title: '租房招聘通 - 找房找工作一站搞定',
      path: '/pages/index/index'
    };
  },

  onShareTimeline() {
    return {
      title: '租房招聘通 - 找房找工作一站搞定'
    };
  }
});
