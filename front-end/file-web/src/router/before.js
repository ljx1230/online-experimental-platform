import router from "./index.js";
import store from "../store/index.js";


router.beforeEach((to, from, next) => {
  // 判断当前登录状态
  store.dispatch("getUserInfo").then(() => {
    if (to.matched.some((m) => m.meta.requireAuth)) {
      if (!store.getters.isLogin) {
        // 没有登录
        next({
          path: "/login",
          query: { Rurl: to.fullPath },
        });
      } else {
        next();
      }
    } else {
      next();
    }
  });
});