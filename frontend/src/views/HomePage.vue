<template>
  <div class="container">
    <!-- 左边栏 -->
    <aside class="sidebar">
      <div class="logo">Vision<span class="logo-x">X</span></div>

      <router-link
        to="/home/fashion"
        class="menu-item"
        :class="{ active: activeFunction === 'model' }"
        @click.native="selectMenu('model')"
      >
    <span class="iconfont icon-shouye-copy"></span>服装模创意生成
</router-link>


      <div v-if="showSubMenu" class="submenu">
        <router-link
        to="/home/fashion/option1"
        class="menu-item sub"
        :class="{ active: activeFunction === '选项1' }"
        @click="selectMenu('选项1')"
      >
      选项1
</router-link>

<router-link
  to="/home/fashion/option2"
  class="menu-item sub"
  :class="{ active: activeFunction === '选项2' }"
  @click="selectMenu('选项2')"
>
  选项2
</router-link>

<router-link
  to="/home/fashion/option3"
  class="menu-item sub"
  :class="{ active: activeFunction === '选项3' }"
  @click="selectMenu('选项3')"
>
  选项3
</router-link>
</div>

      <ul class="funktion-list">
        <router-link
        to="/home/sketch"
        class="menu-item"
        :class="{ active: activeFunction === '线稿成图' }"
        @click="selectMenu('线稿成图')"
        >
        <span class="iconfont icon-xinfeng"></span>线稿成图
        </router-link>

        <router-link
        to="/home/redraw"
        class="menu-item"
        :class="{ active: activeFunction === '局部重绘' }"
        @click="selectMenu('局部重绘')"
        >
        <span class="iconfont icon-denglu"></span>局部重绘
        </router-link>

        <router-link
        to="/home/style-transfer"
        class="menu-item"
        :class="{ active: activeFunction === '风格迁移' }"
        @click="selectMenu('风格迁移')"
        >
        <span class="iconfont icon-denglu"></span>风格迁移
        </router-link>

        <router-link
        to="/home/style-extension"
        class="menu-item"
        :class="{ active: activeFunction === '风格延申' }"
        @click="selectMenu('风格延申')"
        >
        <span class="iconfont icon-denglu"></span>风格延申
        </router-link>

      </ul>
    </aside>

    <!-- 主体 -->
    <main class="main">
      <!-- 顶部栏 -->
      <header class="header">
        <div class="nav">
          <router-link to="/home/render"><span class="iconfont icon-shouye-copy"></span>渲染页面</router-link>
          <router-link to="/home/material"><span class="iconfont icon-shouye-copy"></span>素材管理</router-link>
          <router-link to="/home/design"><span class="iconfont icon-shouye-copy"></span>设计页面</router-link>
          <router-link to="/home/image-processing"><span class="iconfont icon-shouye-copy"></span>图片处理</router-link>
        </div>
        <div class="user-icon-area" @click="showUserPanel = !showUserPanel">
          <span class="user-icon">👤</span>

          <div class="user-panel-float" v-show="showUserPanel" @click.stop>
            <div class="user-info">
              <div class="avatar"></div>
              <div>
                <div>未登录用户</div>
                <div class="user-id">ID: --</div>
              </div>
              <div class="switch">切换</div>
            </div>
            <div class="package-box">
              <div class="package-title">基础套餐</div>
              <div class="bar-label">建模额度 <span>3/100</span></div>
              <div class="bar-bg"><div class="bar-fill" style="width: 3%;"></div></div>
              <div class="bar-label">穿搭额度 <span>10/100</span></div>
              <div class="bar-bg"><div class="bar-fill" style="width: 10%;"></div></div>
              <button class="buy-btn">购买套餐</button>
            </div>
            <div class="user-actions">
              <p>查看订单</p>
              <p>联系客服</p>
              <p>退出登录</p>
            </div>
          </div>
        </div>
      </header>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <input type="text" placeholder="请输入关键词..." />
        <button>提交</button>
      </div>

      <!-- 展示图像区域 -->
      <router-view class="page-view" />
    </main>
  </div>
</template>

<script>
import '../styles/fontClass/iconfont.css' 
export default {
  data() {
    return {
      showSubMenu: false,
      showUserPanel: false,
      activeFunction: '', // 用于标识当前激活的菜单项
    };
  },
  methods: {
    selectMenu(name) {
      this.activeFunction = name;
      if (name === 'model') {
        this.showSubMenu = !this.showSubMenu;
      } else if (!['选项1', '选项2', '选项3'].includes(name)) {
        this.showSubMenu = false;
      }
    },
    handleClickOutside(e) {
      const panel = this.$el.querySelector('.user-panel-float');
      const icon = this.$el.querySelector('.user-icon-area');
      if (!panel.contains(e.target) && !icon.contains(e.target)) {
        this.showUserPanel = false;
      }
    },
  },
  mounted() {
    document.addEventListener('click', this.handleClickOutside);
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleClickOutside);
  },
};
</script>

<style scoped>
.container {
  display: flex;
  height: 100vh;
  font-family: Arial, sans-serif;
  font-size: 14px;
  color: #333;
}

.sidebar {
  width: 220px;
  background: #f7f9fb;
  border-right: 1px solid #ddd;
  padding: 10px;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: #3aa655;
  text-align: center;
  margin-bottom: 20px;
}

.logo-x {
  color: #000;
}

.submenu {
  margin-left: 12px;
}

.menu-item {
  display: block;
  padding: 10px 10px;
  padding-left: 10px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.menu-item.sub {
  font-size: 13px;
  color: #555;
  padding-left: 16px;
}

.menu-item.sub.active {
  font-weight: bold;
  color: #1d4ed8;
}

.menu-item.active {
  background-color: #E5F5FD;
}

.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.header {
  height: 50px;
  border-bottom: 1px solid #ddd;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav a {
  margin-right: 20px;
  color: #333;
  text-decoration: none;
  padding: 25px;
}

.nav a:hover {
  color: #007bff;
}

.user-icon-area {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.user-icon {
  font-size: 20px;
  background: #eee;
  padding: 6px;
  border-radius: 50%;
}

.user-panel-float {
  position: absolute;
  top: 35px;
  right: 0;
  width: 280px;
  background: #fff;
  border: 1px solid #ccc;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 100;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.avatar {
  width: 40px;
  height: 40px;
  background: #ccc;
  border-radius: 50%;
  margin-right: 10px;
}

.user-id {
  font-size: 12px;
  color: #888;
}

.switch {
  margin-left: auto;
  font-size: 12px;
  color: #888;
  cursor: pointer;
}

.package-box {
  background: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.package-title {
  font-weight: bold;
  margin-bottom: 5px;
}

.bar-label {
  font-size: 12px;
  margin: 6px 0 2px;
}

.bar-label span {
  float: right;
}

.bar-bg {
  background: #e0e0e0;
  height: 6px;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 5px;
}

.bar-fill {
  background: #2563eb;
  height: 100%;
}

.buy-btn {
  margin-top: 10px;
  width: 100%;
  background: #6366f1;
  color: white;
  padding: 6px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.user-actions p {
  margin: 8px 0;
  cursor: pointer;
}

.search-bar {
  display: flex;
  padding: 15px 40px;
  border-bottom: 1px solid #eee;
}

.search-bar input {
  height: 45px;
  padding: 0 10px;
  border: 1px solid #ccc;
  border-right: none;
  border-radius: 4px 0 0 4px;
  outline: none;
  font-size: 14px;
  width: 700px;
  box-sizing: border-box;
}

.search-bar button {
  height: 45px;
  padding: 0 16px;
  background-color: #007bff;
  color: white;
  border: 1px solid #007bff;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
  font-size: 14px;
}

.image-area {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.image-area img {
  max-height: 500px;
  max-width: 90%;
  object-fit: contain;
}

.funktion-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.funktion-list .menu-item {
  display: block;
  /* padding: 10px 0; */
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.funktion-list .menu-item:hover {
  background-color: #E5F5FD;
}

.funktion-list .menu-item.active {
  background-color: #E5F5FD;
  font-weight: bold;
  color: #1d4ed8;
}

.funktion-list .menu-item .iconfont {
  margin-right: 8px;
}
.sidebar .menu-item {
  text-decoration: none; /* 去除下划线 */
  color: #333; /* 去除蓝色字体，保持默认字体颜色 */
}

.sidebar .menu-item:hover {
  color: #333; /* 保持hover时颜色不变 */
}

.sidebar .menu-item.active {
  background-color: #E5F5FD;
  color: #333; /* 保持激活项字体颜色为默认颜色 */
}

.sidebar .menu-item.sub.active {
  font-weight: bold;
  color: #1d4ed8; /* 可以选择让子菜单激活时保持原样 */
}

</style>
