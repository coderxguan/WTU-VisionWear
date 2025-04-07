<template>
  <div class="login-container">
    <div class="welcome-message">
      <h1>欢迎使用VisionWear！</h1>
      <p>请输入您的用户名和密码</p>
    </div>
    <el-form :model="loginForm" ref="loginForm" label-width="80px" class="login-form">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="loginForm.username" placeholder="请输入用户名" class="input-field" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input type="password" v-model="loginForm.password" placeholder="请输入密码" class="input-field" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleLogin" class="login-button">登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
      },
    };
  },
  methods: {
    async handleLogin() {
      try {
        const response = await this.$axios.post('/auth/login', this.loginForm);
        console.log(response.data);  // 调试打印，检查返回的结构

        if (response.data && response.data.code === 1 && response.data.data) {
          // 登录成功，保存 token 和其他信息
          const {token, userName, userId} = response.data.data;
          if (token) {
            localStorage.setItem('token', token); // 保存 token
            localStorage.setItem('userName', userName); // 保存用户名
            localStorage.setItem('userId', userId); // 保存用户ID

            // 登录成功后弹窗提示并跳转到首页
            this.$message.success('登录成功');
            this.$router.push({name: 'Home'});
          } else {
            this.$message.error('返回的 token 不存在');
          }
        } else {
          this.$message.error(response.data.msg || '登录失败');
        }
      } catch (error) {
        console.error('登录请求出错', error);
        this.$message.error('登录请求失败，请检查网络连接');
      }
    },
  },
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #b4c6fc, #a5b9f7); /* 淡一点的背景颜色 */
}

.welcome-message {
  text-align: center;
  margin-bottom: 30px;
}

.welcome-message h1 {
  font-size: 2.5em;
  color: #333;
}

.welcome-message p {
  font-size: 1.2em;
  color: #666;
}

.login-form {
  width: 400px;
  padding: 40px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.input-field {
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
  padding: 12px;
  font-size: 16px;
}
</style>
