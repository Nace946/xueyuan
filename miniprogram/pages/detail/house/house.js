const houseService = require('../../../services/house.js');
const { formatHouse } = require('../../../utils/format.js');
const util = require('../../../utils/util.js');

Page({
  data: {
    id: '',
    detail: null,
    loading: true,
    error: false
  },

  onLoad(options) {
    if (!options.id) {
      wx.showToast({ title: '参数缺失', icon: 'none' });
      return;
    }
    this.setData({ id: options.id });
    this.loadDetail();
  },

  async loadDetail() {
    this.setData({ loading: true, error: false });
    try {
      const detail = formatHouse(await houseService.detail(this.data.id));
      // 描述按换行切段展示
      detail.descList = (detail.description || '')
        .split('\n')
        .map((s) => s.trim())
        .filter((s) => s);
      this.setData({ detail, loading: false });
      wx.setNavigationBarTitle({ title: detail.title || '房源详情' });
    } catch (e) {
      this.setData({ loading: false, error: true });
    }
  },

  onPreview(e) {
    const current = e.currentTarget.dataset.src;
    util.previewImage(current, this.data.detail.imageList);
  },

  onCall() {
    util.callPhone(this.data.detail.contactPhone);
  },

  onCopyWechat() {
    const wxid = this.data.detail.contactWechat;
    if (!wxid) {
      wx.showToast({ title: '未填写微信号', icon: 'none' });
      return;
    }
    util.copyText(wxid);
  },

  onCopyAddress() {
    util.copyText(this.data.detail.address);
  },

  onShareAppMessage() {
    const d = this.data.detail || {};
    return {
      title: d.title || '优质房源推荐',
      path: '/pages/detail/house/house?id=' + this.data.id,
      imageUrl: d.cover || ''
    };
  }
});
