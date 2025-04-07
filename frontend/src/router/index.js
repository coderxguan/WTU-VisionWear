import { createRouter, createWebHistory } from 'vue-router';
import LoginPage from '../views/LoginPage.vue';
import HomePage from '../views/HomePage.vue';

const routes = [
    { path: '/home', component: HomePage, name: 'Home' },  // 给首页路由命名为 'Home'
    { path: '/login', component: LoginPage, name: 'Login' },  // 给登录页路由命名为 'Login'
    {
        path: '/*', // 其他所有路径跳转到登录页面
        redirect: '/login',
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
