const houseService = require('../../../services/house.js');
const { formatHouseList } = require('../../../utils/format.js');

const PRICE_RANGES = [
  { label: '不限', value: '', min: '', max: '' },
  { label: '1000以下', value: '0-1000', min: 0, max: 1000 },
  { label: '1000-2000', value: '1000-2000', min: 1000, max: 2000 },
  { label: '2000-3000', value: '2000-3000', min: 2000, max: 3000 },
  { label: '3000-5000', value: '3000-5000', min: 3000, max: 5000 },
  { label: '5000以上', value: '5000-', min: 5000, max: '' }
];

const SORTS = [
  { label: '最新', value: 'latest' },
  { label: '价格从低到高', value: 'price_asc' },
  { label: '价格从高到低', value: 'price_desc' }
];

Page({
  data: {
    list: [],
    districts: [],
    priceRanges: PRICE_RANGES,
    sorts: SORTS,
    keyword: '',
    district: '',
    priceRange: '',
    priceIndex: 0,
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

    const price = this.data.priceRanges[this.data.priceIndex];

    try {
      const data = await houseService.list({
        pageNum: this.data.pageNum,
        pageSize: this.data.pageSize,
        keyword: this.data.keyword,
        district: this.data.district,
        minPrice: price.min,
        maxPrice: price.max,
        sort: this.data.sort
      });

      const newList = formatHouseList(data.list);
      const list = isRefresh ? newList : this.data.list.concat(newList);
      const total = data.total || 0;

      this.setData({
        list,
        total,
        districts: (data.districts || this.data.districts || []),
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

  onPriceTap(e) {
    const index = Number(e.currentTarget.dataset.index);
    if (index === this.data.priceIndex) return;
    this.setData({ priceIndex: index, pageNum: 1, hasMore: true });
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
    return { title: '精选房源 - 租房招聘通', path: '/pages/house/list/list' };
  }
});
