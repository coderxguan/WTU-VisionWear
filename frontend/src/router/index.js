import { createRouter, createWebHistory } from 'vue-router';
import LoginPage from '../views/LoginPage.vue';
import HomePage from '../views/HomePage.vue';

const routes = [
    { path: '/', redirect: '/login' }, // 访问根路径时跳转到登录
    { path: '/home', component: HomePage, name: 'Home' },
    { path: '/login', component: LoginPage, name: 'Login' },
    { path: '/:pathMatch(.*)*', redirect: '/login' }, // 捕获所有未知路径
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
