import { checkUserLoginInfo } from "../../request/user";

export default {
  state: {
    isLogin: false, // 初始时用户未登录
    username: "",
    userId: 0,
    userImgUrl: "",
    userInfoObj: {}
  },
  mutations: {
    changeLogin(state, data) {
      state.isLogin = data;
    },
    changeUsername(state, data) {
      state.username = data;
    },
    changeUserId(state, data) {
      state.userId = data;
    },
    changeUserInfoObj(state, data) {
      state.userInfoObj = Object.assign({}, state.userInfoObj, data);
    },
  },
  actions: {
    getUserInfo(context) {
      return checkUserLoginInfo().then(res => {
        if (res.success) {
          context.commit("changeLogin", res.success);
          context.commit("changeUsername", res.data.username);
          context.commit("changeUserId", res.data.userId);
          context.commit("changeUserInfoObj", res.data)
        } else {
          context.commit("changeLogin", res.success);
        }
      });
    }
  },
  modules: {
  }
}