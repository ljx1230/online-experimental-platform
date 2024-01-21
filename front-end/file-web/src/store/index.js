import Vue from 'vue'
import Vuex from 'vuex'
import user from "./module/user.js";
import file from "./module/file";
import imgReview from "./module/imgReview.js";

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    user,
    file,
    imgReview,
  },
  getters: {
    isLogin: (state) => state.user.isLogin,
    username: (state) => state.user.username,
    userId: (state) => state.user.userId,
    userInfoObj: (state) => state.user.userInfoObj,
    // 需要显示的表格列
    selectedColumnList: (state) =>
      state.file.selectedColumnList === null
        ? state.file.allColumnList
        : state.file.selectedColumnList.split(","),
    // 查看模式 - 0 列表 | 1 网格 | 2 时间线
    showModel: (state) => (state.file.showModel === null ? 0 : Number(state.file.showModel))
  }
})
