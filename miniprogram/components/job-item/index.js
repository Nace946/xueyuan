Component({
  externalClasses: ['item-class'],
  properties: {
    item: {
      type: Object,
      value: {}
    },
    /** 是否为列表最后一项（去掉底部分隔线） */
    last: {
      type: Boolean,
      value: false
    }
  },
  methods: {
    onTap() {
      const id = this.data.item.id;
      if (!id) return;
      wx.navigateTo({
        url: '/pages/detail/job/job?id=' + id
      });
    }
  }
});
