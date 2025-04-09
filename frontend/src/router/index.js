import { createRouter, createWebHistory } from 'vue-router';
import LoginPage from '../views/LoginPage.vue';
import HomePage from '../views/HomePage.vue';
import RegisterPage from "../views/RegisterPage.vue";
import FashionMain from '../views/FashionMain.vue'
import FashionOption1 from '../views/Fashionoption1.vue'
import FashionOption2 from '../views/FashionOption2.vue'
import FashionOption3 from '../views/FashionOption3.vue'
import RenderPage from '../views/RenderPage.vue'
import MaterialPage from '../views/MaterialPage.vue'
import DesignPage from '../views/DesignPage.vue'
import ImageProcessingPage from '../views/ImageProcessingPage.vue'
import SketchToImagePage from '../views/SketchToImagePage.vue'
import PartialRedrawPage from '../views/PartialRedrawPage.vue'
import StyleTransferPage from '../views/StyleTransferPage.vue'
import StyleExtensionPage from '../views/StyleExtensionPage.vue'

const routes = [

    { path: '/', redirect: '/login' }, // 访问根路径时跳转到登录
    { 
      path: '/home', 
      component: HomePage, 
      name: 'Home',
      children: [
        { path: '', component: RenderPage }, // 默认子页面
        { path: 'fashion', component: FashionMain },
        { path: 'fashion/option1', component: FashionOption1 },
        { path: 'fashion/option2', component: FashionOption2 },
        { path: 'fashion/option3', component: FashionOption3 },
        { path: 'render', component: RenderPage },
        { path: 'material', component: MaterialPage },
        { path: 'design', component: DesignPage },
        { path: 'image-processing', component: ImageProcessingPage },
        { path: 'sketch', component: SketchToImagePage },
        { path: 'redraw', component: PartialRedrawPage },
        { path: 'style-transfer', component: StyleTransferPage },
        { path: 'style-extension', component: StyleExtensionPage }
      ]
    },
    { path: '/login', component: LoginPage, name: 'Login' },
    { path: '/register', component: RegisterPage, name: 'Register' }, // 添加注册页面路由
    { path: '/:pathMatch(.*)*', redirect: '/login' }, // 捕获所有未知路径
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
