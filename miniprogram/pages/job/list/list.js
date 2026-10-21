const jobService = require('../../../services/job.js');
const { formatJobList } = require('../../../utils/format.js');

// 社区场景以临时工/兼职为主，排序上把临时放前面
const JOB_TYPES = [
  { label: '不限', value: '' },
  { label: '临时', value: '临时' },
  { label: '兼职', value: '兼职' },
  { label: '全职', value: '全职' },
  { label: '实习', value: '实习' }
];

const SORTS = [
  { label: '最新', value: 'latest' },
  { label: '薪资从高到低', value: 'salary_desc' }
];

Page({
  data: {
    list: [],
    districts: [],
    jobTypes: JOB_TYPES,
    sorts: SORTS,
    keyword: '',
    district: '',
    jobType: '',
    sortIndex: 0,
    sort: 'latest',
    pageNum: 1,
    pageSize: 10,
    total: 0,
    hasMore: true,
    loading: false,
    firstLoading: true
  },

  onLoad(options) {
    if (options.keyword) {
      this.setData({ keyword: options.keyword });
    }
    this.loadList(true);
  },

  onPullDownRefresh() {
    this.setData({ pageNum: 1, hasMore: true });
    this.loadList(true, () => wx.stopPullDownRefresh());
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({ pageNum: this.data.pageNum + 1 });
      this.loadList(false);
    }
  },

  async loadList(isRefresh, done) {
    if (this.data.loading) {
      if (done) done();
      return;
    }
    this.setData({ loading: true });

    try {
      const data = await jobService.list({
        pageNum: this.data.pageNum,
        pageSize: this.data.pageSize,
        keyword: this.data.keyword,
        district: this.data.district,
        jobType: this.data.jobType,
        sort: this.data.sort
      });

      const newList = formatJobList(data.list);
      const list = isRefresh ? newList : this.data.list.concat(newList);
      const total = data.total || 0;

      this.setData({
        list,
        total,
        districts: data.districts || this.data.districts || [],
        hasMore: list.length < total,
        loading: false,
        firstLoading: false
      });
    } catch (e) {
      this.setData({ loading: false, firstLoading: false });
    } finally {
      if (done) done();
    }
  },

  onKeywordInput(e) {
    this.setData({ keyword: e.detail.value });
  },

  onSearch() {
    this.setData({ pageNum: 1, hasMore: true });
    this.loadList(true);
  },

  onDistrictTap(e) {
    const value = e.currentTarget.dataset.value;
    if (value === this.data.district) return;
    this.setData({ district: value, pageNum: 1, hasMore: true });
    this.loadList(true);
  },

  onTypeTap(e) {
    const value = e.currentTarget.dataset.value;
    if (value === this.data.jobType) return;
    this.setData({ jobType: value, pageNum: 1, hasMore: true });
    this.loadList(true);
  },

  onSortTap(e) {
    const index = Number(e.currentTarget.dataset.index);
    if (index === this.data.sortIndex) return;
    this.setData({
      sortIndex: index,
      sort: SORTS[index].value,
      pageNum: 1,
      hasMore: true
    });
    this.loadList(true);
  },

  onShareAppMessage() {
    return { title: '最新招聘 - 租房招聘通', path: '/pages/job/list/list' };
  }
});
