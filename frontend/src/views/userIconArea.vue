<template>
    <slot></slot>
    <div class="user-icon-area" @click="showUserPanel = !showUserPanel">
      <span class="iconfont icon-denglu1 user-icon"></span>
  
      <div class="user-panel-float" v-show="showUserPanel" @click.stop>
        <div class="user-info">
          <div class="avatar">
            <img src="https://img.icons8.com/ios-filled/50/user.png" alt="avatar" />
          </div>
          <div class="user-text">
            <div class="phone">151****9603</div>
            <div class="user-id">用户9603</div>
          </div>
          <div class="switch">⟳ 切换团队</div>
        </div>
  
        <div class="package-box">
          <div class="version">免费版</div>
          <div class="bar-label">算力（每月更新）<span>0/50</span></div>
          <div class="bar-bg"><div class="bar-fill" style="width: 0%;"></div></div>
  
          <div class="bar-label">作图体验点（每月更新）<span>0/200</span></div>
          <div class="bar-bg"><div class="bar-fill" style="width: 0%;"></div></div>
  
          <div class="package-tip">购买套餐获得更多算力，解锁更多权益</div>
          <button class="buy-btn">立即购买套餐⚡</button>
        </div>
  
        <div class="user-actions">
          <p>🎨 创建团队</p>
          <p>🏠 前往官网</p>
          <p>🛒 购买套餐</p>
          <p>⚙️ 个人设置 <span class="blue">绑定微信快速登录</span></p>
          <p>📦 订单中心</p>
          <p>🔑 重置密码</p>
          <p>🚪 退出登录</p>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import '../styles/fontClass/iconfont.css'
  
  export default {
    data() {
      return {
        showUserPanel: false,
        activeFunction: '',
      };
    },
    methods: {
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
  .user-icon-area {
    position: relative;
    display: inline-block;
    cursor: pointer;
  }
  
  .user-icon {
    font-size: 28px;
    margin-right: 100px;
  }
  
  .user-panel-float {
    position: absolute;
    top: 35px;
    right: 0;
    width: 280px;
    background: #fff;
    border: 1px solid #eee;
    padding: 15px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    z-index: 100;
    border-radius: 8px;
  }
  
  .user-info {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
  }
  
  .avatar img {
    width: 40px;
    height: 40px;
    border-radius: 50%;
  }
  
  .user-text {
    flex: 1;
    margin-left: 10px;
  }
  
  .user-text .phone {
    font-weight: bold;
    font-size: 14px;
  }
  
  .user-id {
    color: #999;
    font-size: 12px;
  }
  
  .switch {
    color: #666;
    font-size: 12px;
    cursor: pointer;
  }
  
  .package-box {
    margin-bottom: 16px;
  }
  
  .version {
    font-size: 14px;
    font-weight: bold;
    margin-bottom: 6px;
  }
  
  .bar-label {
    font-size: 12px;
    color: #333;
    display: flex;
    justify-content: space-between;
    margin-top: 8px;
  }
  
  .bar-bg {
    background: #f0f0f0;
    height: 6px;
    border-radius: 4px;
    overflow: hidden;
  }
  
  .bar-fill {
    height: 100%;
    background: #409eff;
    width: 0%;
  }
  
  .package-tip {
    font-size: 12px;
    color: #666;
    margin: 10px 0;
  }
  
  .buy-btn {
    background: linear-gradient(90deg, #7f5eff, #5a8bff);
    border: none;
    border-radius: 4px;
    color: white;
    width: 100%;
    padding: 8px 0;
    font-weight: bold;
    cursor: pointer;
  }
  
  .user-actions p {
    font-size: 14px;
    color: #333;
    margin: 10px 0;
    cursor: pointer;
  }
  
  .user-actions .blue {
    color: #007aff;
    margin-left: 5px;
  }
  </style>
  