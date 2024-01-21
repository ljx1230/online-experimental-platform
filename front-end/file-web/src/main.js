import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import "./assets/style/base.css"
import "./router/before.js"
import uploader from 'vue-simple-uploader'
import all from './libs/globalFunction.js';

Vue.use(ElementUI)
Vue.use(uploader)
Vue.use(all)

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
