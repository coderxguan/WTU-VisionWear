<template>
  <div class="home-container">
    <div class="greeting">
      <h1>欢迎回来，{{ userName }}！</h1>
      <p>这是您的个人主页，您可以在这里查看和管理您的个人信息。</p>
    </div>

    <div class="actions">
      <el-button type="primary" @click="goToProfile">查看个人资料</el-button>
      <el-button type="danger" @click="logout">退出登录</el-button>
    </div>

    <div class="stats">
      <h2>最近活动</h2>
      <ul>
        <li>查看过的产品：10</li>
        <li>最近的消息：5</li>
        <li>待办事项：3</li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userName: localStorage.getItem('userName') || '用户',  // 获取用户名
    };
  },
  methods: {
    logout() {
      // 清除本地存储的用户信息
      localStorage.removeItem('token');
      localStorage.removeItem('userName');
      localStorage.removeItem('userId');

      // 跳转回登录页
      this.$router.push({name: 'Login'});
    },
    goToProfile() {
      // 假设你有个人资料页面
      this.$router.push({name: 'Profile'});
    },
  },
};
</script>

<style scoped>
.home-container {
  padding: 40px;
  background-color: #f4f7fc;
  border-radius: 8px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  max-width: 800px;
  margin: 50px auto;
}

.greeting {
  text-align: center;
  margin-bottom: 30px;
}

.greeting h1 {
  font-size: 2em;
  color: #333;
}

.greeting p {
  font-size: 1.1em;
  color: #555;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 40px;
}

.actions el-button {
  width: 180px;
}

.stats {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.stats h2 {
  font-size: 1.5em;
  margin-bottom: 15px;
}

.stats ul {
  list-style-type: none;
  padding: 0;
}

.stats ul li {
  font-size: 1.1em;
  color: #444;
  padding: 8px 0;
}
</style>
