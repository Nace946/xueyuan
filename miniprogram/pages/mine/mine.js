Page({
  data: {
    version: '1.0.0',
    items: [
      { icon: '🏠', title: '找房', desc: '浏览全部在租房源', url: '/pages/house/list/list', tab: true },
      { icon: '💼', title: '找工作', desc: '浏览全部在招职位', url: '/pages/job/list/list', tab: true }
    ],
    contactPhone: '请在小程序后台配置客服电话',
    workTime: '周一至周日 9:00 - 21:00'
  },

  onItemTap(e) {
    const item = e.currentTarget.dataset.item;
    if (item.tab) {
      wx.switchTab({ url: item.url });
    } else {
      wx.navigateTo({ url: item.url });
    }
  },

  onCall() {
    const phone = this.data.contactPhone;
    if (!/^[\d\-+]+$/.test(phone)) {
      wx.showToast({ title: '请在代码中配置客服电话', icon: 'none' });
      return;
    }
    wx.makePhoneCall({ phoneNumber: phone, fail() {} });
  },

  onShareAppMessage() {
    return { title: '租房招聘通 - 找房找工作一站搞定', path: '/pages/index/index' };
  }
});
