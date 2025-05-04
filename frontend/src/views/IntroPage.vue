<template>
  <div class="intro-page">
    <!-- 导航栏 -->
    <div class="nav-container">
      <div class="container">
        <el-row class="nav-row" justify="space-between" align="middle">
          <el-col :span="8">
            <div class="logo">
              <span class="logo-text">DeepWear</span>
            </div>
          </el-col>
          <el-col :span="16">
            <div class="nav-menu">
              <el-menu mode="horizontal" :ellipsis="false" class="menu-items">
                <el-menu-item index="1">功能</el-menu-item>
                <el-menu-item index="2">价格</el-menu-item>
                <el-menu-item index="3">关于我们</el-menu-item>
              </el-menu>
              <div class="nav-buttons">
                <el-button @click="showLoginDialog" type="text">登录</el-button>
                <el-button @click="showRegisterDialog" type="primary" round>免费注册</el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 英雄区域 -->
    <div class="hero-section">
      <div class="container">
        <el-row :gutter="40">
          <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
            <div class="hero-content">
              <h1 class="hero-title">AI定义时尚，科技塑造未来</h1>
              <p class="hero-description">DeepWear是一个结合AI与时尚的创新平台，为您提供个性化的时尚体验和智能穿搭建议。</p>
              <div class="hero-buttons">
                <el-button type="primary" size="large" round @click="showRegisterDialog">立即开始</el-button>
                <el-button type="info" size="large" round plain>了解更多</el-button>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
            <div class="hero-image">
              <img src="" alt="DeepWear AI时尚" />
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 简单内容区域 -->
    <div class="content-section">
      <div class="container">
        <h2 class="section-title">智能时尚的新时代</h2>
        <p class="section-description">
          DeepWear 利用最先进的人工智能技术，结合对时尚潮流的深入理解，为每位用户提供量身定制的时尚体验。
          无论您是追求个性风格还是希望跟上最新潮流，我们的平台都能满足您的需求。
        </p>
      </div>
    </div>

    <!-- 简化的页脚 -->
    <div class="footer">
      <div class="container">
        <p>&copy; {{ new Date().getFullYear() }} DeepWear. 保留所有权利。</p>
      </div>
    </div>

    <!-- 登录弹窗 -->
    <el-dialog
        v-model="loginDialogVisible"
        title=""
        width="400px"
        :show-close="true"
        :close-on-click-modal="false"
        custom-class="auth-dialog"
    >
      <!-- 引入您现有的登录页面组件 -->
      <LoginPage
          v-if="loginDialogVisible"
          :is-dialog="true"
          @switch-to-register="switchToRegister"
          @switch-to-forgot-password = "switchToForgotPassword"
      />
    </el-dialog>

    <!-- 注册弹窗 -->
    <el-dialog
        v-model="registerDialogVisible"
        title=""
        width="400px"
        :show-close="true"
        :close-on-click-modal="false"
        custom-class="auth-dialog"
    >
      <!-- 引入您现有的注册页面组件 -->
      <RegisterPage
          v-if="registerDialogVisible"
          :is-dialog="true"
          @switch-to-login="switchToLogin"
      />
    </el-dialog>

    <!-- 忘记密码弹窗 -->
    <el-dialog
        v-model="forgotPasswordDialogVisible"
        title=""
        width="400px"
        :show-close="true"
        :close-on-click-modal="false"
        custom-class="auth-dialog"
    >
      <!-- 引入忘记密码页面组件 -->
      <ForgotPasswordPage
          v-if="forgotPasswordDialogVisible"
          :is-dialog="true"
          @switch-to-login="switchToLogin"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import {ref} from 'vue'
// 导入您修改后的登录和注册页面组件
import LoginPage from './LoginPage.vue'
import RegisterPage from './RegisterPage.vue'
import ForgotPasswordPage from "./ForgotPasswordPage.vue";

// 弹窗状态
const loginDialogVisible = ref(false)
const registerDialogVisible = ref(false)
const forgotPasswordDialogVisible = ref(false)

// 显示登录弹窗
const showLoginDialog = () => {
  loginDialogVisible.value = true
  registerDialogVisible.value = false
  forgotPasswordDialogVisible.value = false
}

// 显示注册弹窗
const showRegisterDialog = () => {
  registerDialogVisible.value = true
  loginDialogVisible.value = false
  forgotPasswordDialogVisible.value = false
}

// 切换到登录
const switchToLogin = () => {
  loginDialogVisible.value = true
  registerDialogVisible.value = false
  forgotPasswordDialogVisible.value = false
}

// 切换到注册
const switchToRegister = () => {
  registerDialogVisible.value = true
  loginDialogVisible.value = false
  forgotPasswordDialogVisible.value = false
}
// 显示忘记密码弹窗
const switchToForgotPassword = () => {
  forgotPasswordDialogVisible.value = true
  loginDialogVisible.value = false
  registerDialogVisible.value = false
}
</script>

<style scoped>
/* 全局样式 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 导航栏样式 */
.nav-container {
  height: 80px;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.nav-row {
  height: 80px;
}

.logo {
  display: flex;
  align-items: center;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  background: linear-gradient(135deg, #43cea2, #185a9d);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-menu {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.menu-items {
  background-color: transparent;
  border-bottom: none;
}

.nav-buttons {
  margin-left: 20px;
}

.nav-buttons .el-button--primary {
  background: linear-gradient(135deg, #43cea2, #185a9d);
  border: none;
}

/* 英雄区域样式 */
.hero-section {
  padding: 160px 0 100px;
  background: linear-gradient(135deg, rgba(67, 206, 162, 0.1), rgba(24, 90, 157, 0.1));
}

.hero-content {
  padding-right: 40px;
}

.hero-title {
  font-size: 42px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
  line-height: 1.2;
  background: linear-gradient(135deg, #43cea2, #185a9d);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-description {
  font-size: 18px;
  color: #666;
  margin-bottom: 30px;
  line-height: 1.6;
}

.hero-buttons {
  display: flex;
  gap: 15px;
}

.hero-buttons .el-button--primary {
  background: linear-gradient(135deg, #43cea2, #185a9d);
  border: none;
}

.hero-image img {
  width: 100%;
  max-width: 500px;
  border-radius: 15px;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}

/* 内容区域样式 */
.content-section {
  padding: 80px 0;
  text-align: center;
}

.section-title {
  font-size: 32px;
  margin-bottom: 20px;
  color: #333;
  background: linear-gradient(135deg, #43cea2, #185a9d);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.section-description {
  font-size: 18px;
  color: #666;
  max-width: 800px;
  margin: 0 auto;
  line-height: 1.6;
}

/* 页脚样式 */
.footer {
  padding: 30px 0;
  background-color: #2c3e50;
  color: #fff;
  text-align: center;
}

/* 认证弹窗样式 */
.auth-dialog {
  border-radius: 15px;
  overflow: hidden;
}

.auth-dialog :deep(.el-dialog__header) {
  display: none;
}

.auth-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.auth-dialog :deep(.el-dialog__headerbtn) {
  top: 15px;
  right: 15px;
  z-index: 10;
}

.auth-dialog :deep(.el-dialog__close) {
  color: #333;
  font-size: 18px;
}

/* 响应式样式 */
@media (max-width: 991px) {
  .hero-section {
    padding: 140px 0 80px;
  }

  .hero-content {
    padding-right: 0;
    margin-bottom: 50px;
  }

  .hero-title {
    font-size: 36px;
  }

  .hero-description {
    font-size: 16px;
  }
}

@media (max-width: 767px) {
  .nav-buttons {
    display: none;
  }

  .hero-title {
    font-size: 30px;
  }
}
</style>