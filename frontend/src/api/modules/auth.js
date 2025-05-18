import request from '../request';

/**
 * 用户登录
 * @param {Object} data - 登录信息 {username, password}
 * @returns {Promise} - 请求结果
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  });
}

/**
 * 用户注册
 * @param {Object} data - 注册信息 {username, password, email}
 * @returns {Promise} - 请求结果
 */
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  });
}

/**
 * 忘记密码
 * @param {Object} data - 请求数据 {email, verificationCode?}
 * @returns {Promise} - 请求结果
 */
export function forgotPassword(data) {
  return request({
    url: '/auth/forgot-password',
    method: 'post',
    data
  });
}

/**
 * 发送验证码
 * @param {string} email - 用户邮箱
 * @returns {Promise} - 请求结果
 */
export function sendVerificationCode(email) {
  return request({
    url: '/auth/send-verification-code',
    method: 'post',
    data: { email }
  });
}

export default {
  login,
  register,
  forgotPassword,
  sendVerificationCode
}; 