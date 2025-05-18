import request from '../request';

/**
 * 上传图片
 * @param {FormData} formData - 包含文件的表单数据
 * @returns {Promise} - 请求结果
 */
export function uploadImage(formData) {
  return request({
    url: '/image/upload',
    method: 'post',
    data: formData,
    // 确保请求正确处理formData
    transformRequest: [function (data) {
      // 如果是FormData实例，直接返回，不做转换
      if (data instanceof FormData) {
        return data;
      }
      // 其他情况按默认处理
      return JSON.stringify(data);
    }],
    headers: {
      // 不要手动设置Content-Type，让浏览器自动处理
    }
  });
}

/**
 * 图生图接口
 * @param {Object} data - 请求参数
 * @returns {Promise} - 请求结果
 */
export function imageToImage(data) {
  return request({
    url: '/image/image-to-image',
    method: 'post',
    data
  });
}

/**
 * 文生图接口
 * @param {Object} data - 请求参数
 * @returns {Promise} - 请求结果
 */
export function textToImage(data) {
  return request({
    url: '/image/text-to-image',
    method: 'post',
    data
  });
}

export default {
  uploadImage,
  imageToImage,
  textToImage
}; 