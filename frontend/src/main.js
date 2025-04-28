import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

// 引入 Element Plus 和样式
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';

// 引入 axios
import axios from 'axios';
import {getValidToken} from "./utils/auth.js";

// 配置 axios 的基础 URL
axios.defaults.baseURL = 'http://localhost:8080/api';  // 修改为实际的后端地址
axios.defaults.timeout = 10000;  // 配置超时

// 封装 axios 实例
const request = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 1000 * 30,
    headers: {
        'Content-Type': 'application/json',
        'token': getValidToken()
    }
});

// 创建 Vue 实例并挂载
const app = createApp(App);

// 使用 Element Plus
app.use(ElementPlus);

// 使用路由
app.use(router);

// 将 axios 注入到全局，使用 provide 进行依赖注入
app.config.globalProperties.$axios = axios;

app.mount('#app');

export default request;
