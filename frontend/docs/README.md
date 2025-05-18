# API 使用文档

本文档提供了项目 API 模块的使用说明，帮助开发者正确调用后端服务。

## 目录

- [概述](#概述)
- [模块列表](#模块列表)
  - [auth 模块 - 用户认证](auth.md)
  - [image 模块 - 图像处理](image.md)
  - [user 模块 - 用户管理](user.md)
- [核心配置](#核心配置)
  - [request.js - 请求配置](request.md)
  - [错误处理最佳实践](error-handling.md)

## 概述

项目采用模块化的 API 架构，主要包括:

- `request.js`: 封装了 axios 请求配置，包括拦截器、错误处理等
- `modules/`: 按功能分类的 API 模块

## 如何使用

从对应模块导入需要的 API 函数，例如:

```js
import { login, register } from '@/api/modules/auth';

// 调用登录 API
login(username, password)
  .then(response => {
    // 处理成功响应
  })
  .catch(error => {
    // 处理错误
  });
```

## 通用错误处理

所有 API 请求已在 `request.js` 中配置了统一的错误拦截器，但在使用时仍建议使用 try/catch 或 Promise 的 catch 方法处理特定业务逻辑的错误。详细的错误处理策略请参阅[错误处理最佳实践](error-handling.md)。 