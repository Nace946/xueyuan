Component({
  externalClasses: ['card-class'],
  properties: {
    item: {
      type: Object,
      value: {}
    }
  },
  methods: {
    onTap() {
      const id = this.data.item.id;
      if (!id) return;
      wx.navigateTo({
        url: '/pages/detail/house/house?id=' + id
      });
    }
  }
});
