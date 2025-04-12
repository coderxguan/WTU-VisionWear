import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '../views/HomePage.vue';
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
import IntroPage from "../views/IntroPage.vue";

const routes = [

    { path: '/', component: IntroPage, name: 'Intro' }, // 访问根路径时显示介绍页面
    { path: '/home', component: HomePage, name: 'Home' },
    { path: '/:pathMatch(.*)*', redirect: '/' }, // 捕获所有未知路径，重定向到介绍页面
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
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
