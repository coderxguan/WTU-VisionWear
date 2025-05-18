# request.js - API 请求核心配置

本文档介绍了 API 请求的核心配置文件 `request.js`。该文件封装了 axios，设置了拦截器和统一的错误处理。

## 概述

`request.js` 是整个 API 层的基础，它：

1. 创建并配置 axios 实例
2. 设置请求和响应拦截器
3. 实现统一的错误处理逻辑
4. 提供基本的请求方法（get, post, put, delete 等）

## 主要功能

### 基础配置

```js
const service = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 15000
});
```

默认配置了基础 URL（从环境变量获取或默认为 '/api'）和请求超时时间。

### 请求拦截器

请求拦截器会在每个请求发送前自动添加认证 token（如果存在）：

```js
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);
```

### 响应拦截器

响应拦截器处理常见的 HTTP 状态码和错误：

```js
service.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    const { response } = error;
    
    if (response) {
      // 处理 401 未授权错误
      if (response.status === 401) {
        // 清除 token 并重定向到登录页
      }
      
      // 其他状态码处理...
    } else {
      // 处理网络错误或请求被取消的情况
    }
    
    return Promise.reject(error);
  }
);
```

## 如何直接使用

虽然推荐使用模块化的 API 函数，但如果需要，也可以直接使用 `request.js` 导出的方法：

```js
import request from '@/api/request';

// GET 请求
request.get('/user/info', { params: { id: 123 } })
  .then(response => {
    console.log(response.data);
  });

// POST 请求
request.post('/user/create', { username: 'newuser', email: 'user@example.com' })
  .then(response => {
    console.log(response.data);
  });
```

## 扩展和自定义

如需添加新的请求方法或自定义配置，请修改 `request.js`，避免在组件中直接修改 axios 配置。 