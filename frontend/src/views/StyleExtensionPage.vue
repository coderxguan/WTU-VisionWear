<template>
  <div class="style-extension-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>图灵 · 风格延伸</h2>
        </div>
      </template>

      <el-row :gutter="30">
        <!-- 左侧表单区域 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <el-form :model="formData" label-position="top" :rules="rules" ref="formRef" class="form-content">
            <el-row :gutter="16">
              <!-- 源图像上传部分 -->
              <el-col :span="12">
                <!-- 注意：移除了prop="sourceImage"属性，改为直接检查sourceImageUrl -->
                <el-form-item label="源图像 *" required>
                  <el-upload
                      class="image-uploader"
                      :show-file-list="false"
                      :before-upload="beforeImageUpload"
                      :http-request="uploadImage"
                      :disabled="isLoading"
                  >
                    <img v-if="sourceImageUrl" :src="sourceImageUrl" class="uploaded-image" />
                    <div v-else-if="uploadLoading" class="upload-loading">
                      <el-icon class="is-loading"><Loading /></el-icon>
                      <span>上传中...</span>
                    </div>
                    <div v-else class="upload-placeholder">
                      <el-icon class="upload-icon"><Plus /></el-icon>
                      <span>上传源图像 (必填)</span>
                    </div>
                  </el-upload>
                  <div class="form-tip" v-if="!sourceImageUrl">请上传源图像，这是生成图片的基础</div>
                  <div class="form-success" v-else>
                    <el-icon><Check /></el-icon> 图片已上传成功
                  </div>
                </el-form-item>
              </el-col>

              <!-- 参数设置 -->
              <el-col :span="12">
                <el-form-item label="样式预设">
                  <el-select v-model="formData.style" placeholder="请选择样式预设" style="width: 100%">
                    <el-option label="默认" value=""></el-option>
                    <el-option
                        v-for="style in styleOptions"
                        :key="style"
                        :label="style"
                        :value="style"
                    />
                  </el-select>
                </el-form-item>

                <el-form-item label="图像强度">
                  <el-tooltip content="保留原图的程度，值越低越接近原图，范围0-1" placement="top">
                    <el-slider
                        v-model="formData.imageStrength"
                        :min="0"
                        :max="1"
                        :step="0.01"
                        :format-tooltip="value => value.toFixed(2)"
                    ></el-slider>
                  </el-tooltip>
                </el-form-item>

                <el-form-item label="随机种子">
                  <el-input-number
                      v-model="formData.seed"
                      :min="-1"
                      controls-position="right"
                      style="width: 100%"
                      placeholder="-1表示随机"
                  ></el-input-number>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="提示词 *" prop="prompt" required>
              <el-input
                  v-model="formData.prompt"
                  type="textarea"
                  :rows="3"
                  placeholder="描述您想要的图像风格和内容（必填项）"
              ></el-input>
              <div class="form-tip">提示词越详细，生成的图像效果越好</div>
            </el-form-item>

            <el-form-item label="负面提示词">
              <el-input
                  v-model="formData.negativePrompt"
                  type="textarea"
                  :rows="2"
                  placeholder="描述您不希望出现在图片中的元素"
              ></el-input>
            </el-form-item>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="采样步数">
                  <el-slider
                      v-model="formData.steps"
                      :min="10"
                      :max="150"
                      :step="1"
                  ></el-slider>
                  <div class="slider-value">{{ formData.steps }}</div>
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="提示词相关性">
                  <el-slider
                      v-model="formData.cfgScale"
                      :min="1"
                      :max="35"
                      :step="0.5"
                  ></el-slider>
                  <div class="slider-value">{{ formData.cfgScale }}</div>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item>
              <div class="form-check-list" v-if="!isFormValid">
                <strong>生成前请确保：</strong>
                <ul>
                  <li :class="sourceImageUrl ? 'is-valid' : 'is-invalid'">
                    已上传源图像
                  </li>
                  <li :class="formData.prompt.length >= 3 ? 'is-valid' : 'is-invalid'">
                    已填写有效的提示词
                  </li>
                </ul>
              </div>
              <el-button
                  type="primary"
                  :loading="isLoading"
                  @click="generateImage"
                  style="width: 100%"
              >
                {{ isLoading ? '生成中...' : '生成图片' }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-col>

        <!-- 右侧结果展示区 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <div class="result-container">
            <h3 class="result-title">
              生成结果
              <span v-if="generationTime" class="generation-time">耗时: {{ generationTime }}ms</span>
            </h3>

            <!-- 空状态展示 -->
            <template v-if="!isLoading && !resultImage && !errorMessage">
              <div class="placeholder-image">
                <div class="empty-content">
                  <el-icon class="empty-icon"><Picture /></el-icon>
                  <p>上传源图像，填写提示词后点击'生成图片'开始创作</p>
                </div>
              </div>
            </template>

            <!-- 加载中状态 -->
            <template v-if="isLoading">
              <div class="loading-container">
                <el-skeleton style="width: 100%" animated>
                  <template #template>
                    <el-skeleton-item variant="image" style="width: 100%; height: 360px" />
                  </template>
                </el-skeleton>
                <div class="loading-text">
                  <el-icon class="is-loading"><Loading /></el-icon>
                  <span>正在生成您的图片，请稍候...</span>
                </div>
              </div>
            </template>

            <!-- 错误状态 -->
            <template v-if="errorMessage">
              <div class="error-container">
                <el-alert
                    :title="errorMessage"
                    type="error"
                    show-icon
                    :closable="false"
                ></el-alert>
              </div>
            </template>

            <!-- 图片展示状态 -->
            <template v-if="!isLoading && resultImage">
              <div class="image-wrapper">
                <el-image
                    :src="resultImage.imageUrl"
                    fit="contain"
                    :preview-src-list="[resultImage.imageUrl]"
                    class="generated-image"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                      <div>图片加载失败</div>
                    </div>
                  </template>
                </el-image>

                <div class="image-info">
                  <div class="info-item">
                    <span class="info-label">尺寸:</span>
                    <span>{{ resultImage.width }} × {{ resultImage.height }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">种子:</span>
                    <span>{{ resultImage.seed }}</span>
                  </div>
                </div>
              </div>

              <div class="image-actions">
                <el-button type="primary" @click="downloadImage" :icon="Download">
                  下载图片
                </el-button>
                <el-button type="info" @click="copyImageUrl" :icon="CopyDocument">
                  复制链接
                </el-button>
              </div>
            </template>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'
import { Plus, Picture, Loading, Download, CopyDocument } from '@element-plus/icons-vue'
import { getValidToken } from "../utils/auth.js"
import request from "../main.js"

// 样式预设选项
const styleOptions = [
  '3d-model', 'analog-film', 'anime', 'cinematic', 'comic-book',
  'digital-art', 'enhance', 'fantasy-art', 'isometric', 'line-art',
  'low-poly', 'modeling-compound', 'neon-punk', 'origami', 'photographic',
  'pixel-art', 'tile-texture'
]

// 表单引用
const formRef = ref(null)

// 状态变量
const isLoading = ref(false)
const sourceImageUrl = ref('')
const resultImage = ref(null)
const errorMessage = ref('')
const generationTime = ref(null)
const requestId = ref('')
const uploadLoading = ref(false)

// 表单验证规则
const rules = {
  sourceImage: [
    { required: true, message: '请上传源图像', trigger: 'change' }
  ],
  prompt: [
    { required: true, message: '请输入提示词', trigger: 'blur' },
    { min: 3, message: '提示词至少需要3个字符', trigger: 'blur' }
  ]
}

// 表单数据，与DTO结构对应
const formData = reactive({
  prompt: '',
  negativePrompt: '',
  cfgScale: 7.0,
  imageStrength: 0.35,
  steps: 30,
  style: '',
  seed: -1,
  sourceImage: null // 用于表单验证
})

// 计算表单是否有效
const isFormValid = computed(() => {
  return sourceImageUrl.value && formData.prompt.length >= 3
})

// 上传前校验
const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }

  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }

  return true
}

// 自定义上传
// 修改uploadImage函数的相关部分
const uploadImage = async (options) => {
  uploadLoading.value = true
  try {
    const token = getValidToken()
    if (!token) {
      throw new Error('未获取到有效的JWT Token，请重新登录')
    }

    console.log('Token获取成功:', token.substring(0, 10) + '...')

    // 注意：确保字段名为'file'
    const formData = new FormData()
    formData.append('file', options.file)

    // 调试日志
    console.log('准备上传文件:', {
      fileName: options.file.name,
      fileSize: options.file.size,
      fileType: options.file.type
    })

    // 使用FormData作为参数，不要设置Content-Type
    const response = await request({
      method: 'post',
      url: '/image/upload',
      data: formData,
      headers: {
        // 不要手动设置Content-Type，让浏览器自动处理
      },
      // 确保请求正确处理formData
      transformRequest: [function (data) {
        // 如果是FormData实例，直接返回，不做任何转换
        if (data instanceof FormData) {
          return data;
        }
        // 其他情况按默认处理
        return JSON.stringify(data);
      }]
    })

    console.log('上传响应:', response)

    if (response.data.code === 1 && response.data.data) {
      sourceImageUrl.value = response.data.data
      
      // 添加调试日志
      console.log('上传成功，获取到的图片URL:', sourceImageUrl.value)

      // 关键修复：设置sourceImage值并主动清除验证错误
      formData.sourceImage = "uploaded"  // 设置为非空值

      // 延迟一下再验证，确保DOM更新
      setTimeout(() => {
        if (formRef.value) {
          // 清除sourceImage字段的验证错误
          formRef.value.clearValidate('sourceImage')
        }
      }, 100)

      ElMessage.success({
        message: '图片上传成功，请继续填写其他参数',
        type: 'success',
        duration: 3000
      })
    } else {
      ElMessage.error({
        message: '图片上传失败: ' + (response.data.msg || '未知错误'),
        type: 'error',
        duration: 5000,
        showClose: true
      })
    }
  } catch (error) {
    console.error('上传错误:', error)

    // 增强错误日志
    if (error.response) {
      console.error('服务器响应:', {
        status: error.response.status,
        headers: error.response.headers,
        data: error.response.data
      })
    } else if (error.request) {
      console.error('请求已发送但没有响应:', error.request)
    }

    ElMessage.error(error.message || '图片上传失败，请重试')
  } finally {
    uploadLoading.value = false
  }
}

// 生成图像
const generateImage = async () => {
  // 先检查是否上传了源图片 - 直接使用sourceImageUrl值判断
  if (!sourceImageUrl.value) {
    ElMessage.warning({
      message: '请先上传源图片',
      type: 'warning',
      duration: 3000,
      showClose: true
    })
    return
  }

  // 检查提示词是否填写 - 单独检查这个必填项
  if (!formData.prompt || formData.prompt.trim().length < 3) {
    ElMessage.warning({
      message: '请填写有效的提示词(至少3个字符)',
      type: 'warning',
      duration: 3000,
      showClose: true
    })
    return
  }

  // 不再使用formRef.validate进行验证，而是直接判断关键参数

  isLoading.value = true
  resultImage.value = null
  generationTime.value = null
  errorMessage.value = ''

  try {
    // 获取token
    const token = getValidToken()
    if (!token) {
      throw new Error('未登录，请先登录！')
    }

    console.log('Token获取成功:', token.substring(0, 10) + '...')

    // 准备请求体数据并进行参数检查
    const requestBody = {
      sourceImageUrl: sourceImageUrl.value, // 修正参数名称，使用sourceImageUrl
      prompt: formData.prompt.trim(), // 去除空格
      negativePrompt: formData.negativePrompt,
      cfgScale: formData.cfgScale,
      imageStrength: formData.imageStrength,
      samples: 1, // 固定只生成一张图
      steps: formData.steps,
      style: formData.style,
      seed: formData.seed === -1 ? null : formData.seed
    }

    // 日志输出请求参数（便于调试）
    console.log('请求参数:', JSON.stringify(requestBody))

    // 发送请求
    const response = await request({
      method: 'post',
      url: '/image/image-to-image',
      data: requestBody
    })

    console.log('响应状态:', response.status)
    console.log('响应数据:', JSON.stringify(response.data))

    if (response.data.code === 0 && response.data.data) {
      // 按照VO结构处理返回数据
      const result = response.data.data
      requestId.value = result.requestId
      generationTime.value = result.generationTimeMs

      // 显示第一张生成的图片
      if (result.images && result.images.length > 0) {
        resultImage.value = result.images[0]
        ElNotification({
          title: '生成成功',
          message: '图片已成功生成',
          type: 'success',
          position: 'bottom-right'
        })
      } else {
        errorMessage.value = '没有生成任何图像'
        ElMessage.warning(errorMessage.value)
      }
    } else {
      errorMessage.value = response.data.msg || '生成失败'
      ElMessage.error({
        message: errorMessage.value,
        type: 'error',
        duration: 5000,
        showClose: true
      })
    }
  } catch (error) {
    console.error('API调用错误:', error)

    // 增强错误日志
    if (error.response) {
      console.error('服务器响应:', {
        status: error.response.status,
        headers: error.response.headers,
        data: error.response.data
      })

      // 针对不同的HTTP状态码给出更具体的错误信息
      if (error.response.status === 401) {
        errorMessage.value = '登录已过期，请重新登录'
      } else if (error.response.status === 400) {
        errorMessage.value = '请求参数错误：' + (error.response.data.msg || '参数格式不正确')
      } else {
        errorMessage.value = '服务器错误：' + (error.response.data.msg || '未知错误')
      }
    } else if (error.request) {
      console.error('请求已发送但没有响应:', error.request)
      errorMessage.value = '网络连接问题，服务器没有响应'
    } else {
      errorMessage.value = error.message || '发送请求时出错'
    }

    ElMessage.error({
      message: errorMessage.value,
      type: 'error',
      duration: 5000,
      showClose: true
    })
  } finally {
    isLoading.value = false
  }
}

// 下载图像
const downloadImage = () => {
  if (!resultImage.value || !resultImage.value.imageUrl) return

  const a = document.createElement('a')
  a.href = resultImage.value.imageUrl
  a.download = `风格延伸_${Date.now()}.jpg`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)

  ElMessage.success('图片下载已开始')
}

// 复制图片链接
const copyImageUrl = () => {
  if (!resultImage.value || !resultImage.value.imageUrl) return

  navigator.clipboard.writeText(resultImage.value.imageUrl)
      .then(() => {
        ElMessage.success('图片链接已复制到剪贴板')
      })
      .catch(() => {
        ElMessage.error('复制失败，请手动复制')
      })
}
</script>

<style scoped>
.style-extension-container {
  padding: 0 10px;
  margin-left: 10px;
  width: 96%;
}

.el-card {
  border: none;
  box-shadow: none !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(106, 152, 233, 0.15);
  margin-bottom: 5px;
}

.card-header h2 {
  margin: 0;
  font-size: 1.8rem;
  color: var(--el-text-color-primary);
  font-weight: 500;
}

/* 表单内容 */
.form-content {
  padding-right: 10px;
}

/* 滑块值显示 */
.slider-value {
  text-align: center;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  margin-top: 2px;
}

/* 上传样式 */
.image-uploader {
  display: flex;
  justify-content: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  width: 100%;
  height: 160px;
}

.image-uploader:hover {
  border-color: #409EFF;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  color: #8c939d;
}

.upload-loading {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  color: #409EFF;
}

.upload-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.uploaded-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

/* 上传成功的提示样式 */
.form-success {
  font-size: 12px;
  color: #67C23A;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
}

/* 结果容器样式 */
.result-container {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.result-title {
  width: 100%;
  margin-top: 0;
  margin-bottom: 12px;
  font-size: 16px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  text-align: left;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.generation-time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  font-weight: normal;
}

/* 图片占位符样式 */
.placeholder-image {
  width: 100%;
  height: 360px;
  background-color: rgba(106, 152, 233, 0.05);
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: var(--el-box-shadow-light);
}

.empty-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: var(--el-text-color-secondary);
}

.empty-icon {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

/* 加载中样式 */
.loading-container {
  width: 100%;
}

.loading-text {
  margin-top: 15px;
  text-align: center;
  color: var(--el-text-color-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

/* 错误样式 */
.error-container {
  width: 100%;
  height: auto;
  padding: 20px 0;
}

/* 图片包装器 */
.image-wrapper {
  width: 100%;
  position: relative;
}

/* 生成的图片样式 */
.generated-image {
  width: 100%;
  height: 360px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: var(--el-box-shadow-light);
}

.image-error {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  height: 200px;
}

.image-error .el-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

/* 图片信息 */
.image-info {
  display: flex;
  justify-content: space-between;
  margin: 8px 0;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.info-label {
  color: var(--el-text-color-secondary);
  font-weight: 500;
}

/* 图片操作按钮 */
.image-actions {
  margin-top: 12px;
  display: flex;
  gap: 12px;
  justify-content: center;
  width: 100%;
}

/* 必填项标记样式 */
.el-form-item.is-required .el-form-item__label::before {
  content: '*';
  color: #F56C6C;
  margin-right: 4px;
}

/* 提示文字样式 */
.form-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
  margin-top: 4px;
}

/* 表单验证失败的高亮样式 */
.el-form-item.is-error .image-uploader {
  border-color: #F56C6C;
}

/* 添加一个在生成前的检查列表，显示在按钮上方 */
.form-check-list {
  margin: 10px 0;
  padding: 8px 12px;
  background-color: rgba(64, 158, 255, 0.1);
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
}

.form-check-list ul {
  margin: 5px 0 0 0;
  padding-left: 20px;
}

.form-check-list li {
  margin-bottom: 3px;
}

.form-check-list li.is-valid {
  color: #67C23A;
}

.form-check-list li.is-invalid {
  color: #F56C6C;
}

/* 响应式样式 */
@media (max-width: 991px) {
  .el-row {
    flex-direction: column;
  }

  .el-col {
    width: 100% !important;
    max-width: 100% !important;
    flex: 0 0 100% !important;
  }

  .result-container {
    margin-top: 20px;
  }

  .placeholder-image,
  .generated-image {
    height: 300px;
  }
}

@media (max-width: 768px) {
  .image-actions {
    flex-direction: column;
    width: 100%;
  }

  .image-actions .el-button {
    width: 100%;
  }
}
</style>