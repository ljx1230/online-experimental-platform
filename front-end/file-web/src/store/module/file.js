
export default {
  state: {
    allColumnList: ["extendName", "fileSize", "uploadTime"], // 所有可供选择的表格列
    selectedColumnList: sessionStorage.getItem("selectedColumnList"), //  需要显示的表格列
    showModel: sessionStorage.getItem('showModel'), //  查看模式 - 0 列表 | 1 网格 | 2 时间线
  },
  mutations: {
    // 改变需要显示的表格列
    changeSelectedColumnList(state, data) {
      sessionStorage.setItem("selectedColumnList", data.toString());
      state.selectedColumnList = data.toString();
    },
    // 切换查看模式
    changeShowModel(state, data) {
      sessionStorage.setItem('showModel', data)
      state.showModel = data
    }
  },
  actions: {
  },
}