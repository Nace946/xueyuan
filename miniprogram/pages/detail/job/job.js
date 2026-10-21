const jobService = require('../../../services/job.js');
const { formatJob } = require('../../../utils/format.js');
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
      const detail = formatJob(await jobService.detail(this.data.id));
      detail.descList = (detail.description || '')
        .split('\n')
        .map((s) => s.trim())
        .filter((s) => s);
      this.setData({ detail, loading: false });
      wx.setNavigationBarTitle({ title: detail.title || '职位详情' });
    } catch (e) {
      this.setData({ loading: false, error: true });
    }
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
      title: (d.companyName || '') + ' 招聘 ' + (d.title || ''),
      path: '/pages/detail/job/job?id=' + this.data.id
    };
  }
});
