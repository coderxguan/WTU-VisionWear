# 错误处理最佳实践

本文档介绍项目中的错误处理机制和最佳实践方式。

## 错误处理架构

项目采用了多层次的错误处理机制：

1. **全局拦截器** - 在 `request.js` 中配置的响应拦截器
2. **API 模块层** - 在各 API 模块中处理特定错误
3. **组件层** - 在组件中处理业务逻辑相关错误

## 全局错误拦截

在 `request.js` 中，我们配置了全局错误拦截器处理常见错误：

```js
service.interceptors.response.use(
  response => response,
  error => {
    const { response } = error;
    
    if (response) {
      // 处理 HTTP 状态码错误
      switch (response.status) {
        case 401:
          // 未授权，清除 token 并重定向到登录页
          localStorage.removeItem('token');
          router.push('/login');
          break;
        case 403:
          // 权限不足
          ElMessage.error('权限不足，无法访问此资源');
          break;
        case 404:
          // 资源不存在
          ElMessage.error('请求的资源不存在');
          break;
        case 500:
          // 服务器错误
          ElMessage.error('服务器错误，请稍后再试');
          break;
        default:
          // 其他错误
          ElMessage.error(response.data.message || '请求失败');
      }
    } else if (error.request) {
      // 请求发出但未收到响应
      ElMessage.error('网络错误，无法连接到服务器');
    } else {
      // 请求配置错误
      ElMessage.error('请求配置错误');
    }
    
    return Promise.reject(error);
  }
);
```

## 在 API 模块中处理错误

在 API 模块中，我们可以针对特定业务逻辑处理错误：

```js
// 在 auth.js 模块中
export async function login(username, password) {
  try {
    const response = await request.post('/auth/login', { username, password });
    return response;
  } catch (error) {
    // 处理登录特定错误
    if (error.response && error.response.status === 401) {
      ElMessage.error('用户名或密码错误');
    }
    throw error; // 继续抛出错误供组件处理
  }
}
```

## 在组件中处理错误

在组件中，使用 try/catch 或 Promise 的 catch 方法处理错误：

### 方法一：使用 async/await 和 try/catch

```js
async loginUser() {
  try {
    const response = await login(this.username, this.password);
    // 登录成功处理
    localStorage.setItem('token', response.data.token);
    this.$router.push('/dashboard');
  } catch (error) {
    // 只处理组件级别的错误，通用错误已被拦截器处理
    console.error('登录过程中发生错误:', error);
    // 可以显示自定义错误提示或执行其他逻辑
  }
}
```

### 方法二：使用 Promise 链

```js
uploadImage(this.selectedFile)
  .then(response => {
    // 成功处理
    this.imageUrl = response.data.url;
  })
  .catch(error => {
    // 特定错误处理
    if (error.response && error.response.status === 413) {
      this.$message.error('文件太大，请选择小于 5MB 的图片');
    }
    // 其他错误已被全局拦截器处理
  })
  .finally(() => {
    // 无论成功失败都执行
    this.loading = false;
  });
```

## 错误处理最佳实践

1. **合理分层** - 通用错误在拦截器处理，业务错误在模块或组件中处理
2. **提供反馈** - 始终向用户提供明确的错误提示，避免技术性错误信息
3. **错误恢复** - 提供错误发生后的恢复机制，如重试、刷新或回退
4. **日志记录** - 记录错误以便后续分析和改进
5. **降级处理** - 当关键服务不可用时，提供降级功能

## 常见错误类型及处理

| 错误类型 | 表现 | 处理方式 |
|---------|------|---------|
| 网络错误 | 请求无法发送或无响应 | 提示网络连接问题，建议用户检查网络或稍后重试 |
| 认证错误 (401) | 用户未登录或 token 过期 | 清除失效凭证，重定向到登录页 |
| 授权错误 (403) | 用户权限不足 | 提示权限不足，隐藏或禁用需要高权限的功能 |
| 资源错误 (404) | 请求的资源不存在 | 提示资源不存在，提供返回或搜索选项 |
| 服务器错误 (500) | 服务器内部错误 | 提示系统错误，记录详细日志并提供重试选项 |
| 输入验证错误 | 用户输入不符合要求 | 显示具体字段的错误提示，引导用户修正 |
| 业务规则错误 | 操作违反业务规则 | 展示友好的业务错误提示，说明原因和解决方法 | 