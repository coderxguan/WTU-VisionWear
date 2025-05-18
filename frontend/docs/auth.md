# Auth 模块

auth 模块提供用户认证相关的 API 函数。

## 导入方式

```js
import { login, register, logout } from '@/api/modules/auth';
```

## API 函数

### login(username, password)

用户登录。

**参数:**
- `username`: 用户名
- `password`: 密码

**返回:**
- Promise 对象，解析为包含用户信息和 token 的对象

**示例:**
```js
login('user@example.com', 'password123')
  .then(res => {
    console.log('登录成功:', res.data);
    // 存储 token
    localStorage.setItem('token', res.data.token);
  })
  .catch(error => {
    console.error('登录失败:', error.message);
  });
```

### register(userData)

注册新用户。

**参数:**
- `userData`: 包含用户信息的对象
  - `username`: 用户名
  - `password`: 密码
  - `email`: 电子邮件
  - 其他可选字段

**返回:**
- Promise 对象，解析为注册结果

**示例:**
```js
const userData = {
  username: 'newuser',
  password: 'password123',
  email: 'user@example.com'
};

register(userData)
  .then(res => {
    console.log('注册成功:', res.data);
  })
  .catch(error => {
    console.error('注册失败:', error.message);
  });
```

### logout()

用户登出。

**参数:** 无

**返回:**
- Promise 对象，解析为登出结果

**示例:**
```js
logout()
  .then(() => {
    console.log('登出成功');
    // 清除本地存储的 token
    localStorage.removeItem('token');
  })
  .catch(error => {
    console.error('登出失败:', error.message);
  });
``` 