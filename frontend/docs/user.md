# User 模块

user 模块提供用户信息管理相关的 API 函数。

## 导入方式

```js
import { getUserInfo, updateUserInfo, getUserList } from '@/api/modules/user';
```

## API 函数

### getUserInfo(userId)

获取用户信息。

**参数:**
- `userId`: 可选，用户 ID。若不提供，则获取当前登录用户的信息

**返回:**
- Promise 对象，解析为包含用户信息的对象

**示例:**
```js
// 获取当前登录用户信息
getUserInfo()
  .then(res => {
    console.log('用户信息:', res.data);
  })
  .catch(error => {
    console.error('获取用户信息失败:', error.message);
  });

// 获取指定用户信息
getUserInfo('user123')
  .then(res => {
    console.log('指定用户信息:', res.data);
  })
  .catch(error => {
    console.error('获取指定用户信息失败:', error.message);
  });
```

### updateUserInfo(userData)

更新用户信息。

**参数:**
- `userData`: 包含用户信息的对象，可包含以下字段:
  - `username`: 用户名
  - `email`: 电子邮件
  - `avatar`: 头像 URL
  - `bio`: 个人简介
  - 其他用户相关字段

**返回:**
- Promise 对象，解析为更新后的用户信息

**示例:**
```js
const updatedInfo = {
  username: 'newUsername',
  bio: '这是我的新个人简介',
  avatar: 'https://example.com/avatar.jpg'
};

updateUserInfo(updatedInfo)
  .then(res => {
    console.log('用户信息更新成功:', res.data);
  })
  .catch(error => {
    console.error('用户信息更新失败:', error.message);
  });
```

### getUserList(params)

获取用户列表，通常用于管理员界面。

**参数:**
- `params`: 可选的查询参数
  - `page`: 页码，默认 1
  - `limit`: 每页数量，默认 10
  - `sortBy`: 排序字段
  - `order`: 排序方式，'asc' 或 'desc'
  - `search`: 搜索关键词

**返回:**
- Promise 对象，解析为包含用户列表和分页信息的对象

**示例:**
```js
const params = {
  page: 1,
  limit: 20,
  search: 'john',
  sortBy: 'createdAt',
  order: 'desc'
};

getUserList(params)
  .then(res => {
    console.log('用户列表:', res.data.users);
    console.log('总数:', res.data.total);
  })
  .catch(error => {
    console.error('获取用户列表失败:', error.message);
  });
```

## 注意事项

- 调用获取用户列表 API 需要管理员权限
- 更新用户信息时，只需提供要更新的字段，无需提供完整用户对象
- 部分敏感操作（如修改密码）可能需要单独的 API 调用和额外的验证 