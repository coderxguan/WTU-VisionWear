// utils/index.js
// 导入工具模块
import * as auth from './auth.js';
import * as errorHandler from './errorHandler.js';

// 导出模块
export { auth, errorHandler };

// 默认导出
export default {
  auth,
  errorHandler
};
