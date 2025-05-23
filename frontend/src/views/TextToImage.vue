<template>
  <div class="text-to-image-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>图灵 · 文绘引擎</h2>
        </div>
      </template>

      <el-row :gutter="30">
        <!-- 输入表单 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <el-form :model="formData" label-position="top" :rules="rules" ref="formRef" class="form-content">
            <el-form-item label="提示词" prop="prompt">
              <el-input
                  v-model="formData.prompt"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入您想要生成的图片描述，例如：一幅美丽的山水画，有高山流水和朝霞"
              ></el-input>
            </el-form-item>

            <el-form-item label="负面提示词">
              <el-input
                  v-model="formData.negativePrompt"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入您不希望出现在图片中的元素，例如：模糊，扭曲，低质量"
              ></el-input>
            </el-form-item>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="生成数量">
                  <el-input-number
                      v-model="formData.samples"
                      :min="1"
                      :max="4"
                      :step="1"
                      controls-position="right"
                  ></el-input-number>
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="风格">
                  <el-select v-model="formData.style" placeholder="请选择风格" style="width: 100%">
                    <el-option label="默认" value=""></el-option>
                    <el-option label="模拟胶片" value="analog-film"></el-option>
                    <el-option label="动漫风格" value="anime"></el-option>
                    <el-option label="电影风格" value="cinematic"></el-option>
                    <el-option label="漫画书风格" value="comic-book"></el-option>
                    <el-option label="数字艺术" value="digital-art"></el-option>
                    <el-option label="增强" value="enhance"></el-option>
                    <el-option label="幻想艺术" value="fantasy-art"></el-option>
                    <el-option label="等轴测图" value="isometric"></el-option>
                    <el-option label="线条艺术" value="line-art"></el-option>
                    <el-option label="低多边形" value="low-poly"></el-option>
                    <el-option label="建模复合物" value="modeling-compound"></el-option>
                    <el-option label="霓虹朋克" value="neon-punk"></el-option>
                    <el-option label="折纸风格" value="origami"></el-option>
                    <el-option label="摄影风格" value="photographic"></el-option>
                    <el-option label="像素艺术" value="pixel-art"></el-option>
                    <el-option label="3D模型" value="3d-model"></el-option>
                    <el-option label="瓦片纹理" value="tile-texture"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="宽度">
                  <el-select v-model="formData.width" style="width: 100%">
                    <el-option label="512px" :value="512"></el-option>
                    <el-option label="768px" :value="768"></el-option>
                    <el-option label="1024px" :value="1024"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="高度">
                  <el-select v-model="formData.height" style="width: 100%">
                    <el-option label="512px" :value="512"></el-option>
                    <el-option label="768px" :value="768"></el-option>
                    <el-option label="1024px" :value="1024"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="迭代步数">
                  <el-slider
                      v-model="formData.steps"
                      :min="20"
                      :max="50"
                      :step="1"
                      show-input
                  ></el-slider>
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="提示词相关性">
                  <el-slider
                      v-model="formData.cfgScale"
                      :min="1"
                      :max="30"
                      :step="0.5"
                      show-input
                  ></el-slider>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item>
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

        <!-- 结果展示区 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <div class="result-container">
            <!-- 空状态展示 -->
            <template v-if="!isLoading && !imageUrl && !errorMessage">
              <h3 class="result-title">生成结果</h3>
              <div class="placeholder-image">
                <div class="empty-content">
                  <!-- 内联SVG占位图 -->
                  <svg class="placeholder-svg" width="120" height="120" viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <rect width="120" height="120" rx="8" fill="#F2F6FC" />
                    <path d="M80 40H40C35.5817 40 32 43.5817 32 48V72C32 76.4183 35.5817 80 40 80H80C84.4183 80 88 76.4183 88 72V48C88 43.5817 84.4183 40 80 40Z" stroke="#DCDFE6" stroke-width="2" />
                    <path d="M32 72L50 55L61 65L73 54L88 68" stroke="#C0C4CC" stroke-width="2" />
                    <circle cx="48" cy="52" r="6" fill="#E4E7ED" />
                    <path d="M60 93H48M72 93H60" stroke="#DCDFE6" stroke-width="2" stroke-linecap="round" />
                    <path d="M60 93V80" stroke="#DCDFE6" stroke-width="2" />
                  </svg>
                  <p>填写左侧表单并点击'生成图片'开始创作</p>
                </div>
              </div>
            </template>

            <!-- 加载中状态 -->
            <template v-if="isLoading">
              <h3 class="result-title">生成结果</h3>
              <div class="loading-container">
                <el-skeleton style="width: 100%" animated>
                  <template #template>
                    <el-skeleton-item variant="image" style="width: 100%; height: 480px" />
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
              <h3 class="result-title">生成结果</h3>
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
            <template v-if="!isLoading && imageUrl">
              <h3 class="result-title">生成结果</h3>
              <div class="image-wrapper">
                <el-image
                    :src="imageUrl"
                    fit="contain"
                    :preview-src-list="[imageUrl]"
                    class="generated-image"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                      <div>图片加载失败</div>
                    </div>
                  </template>
                </el-image>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'
import { Picture, Loading, Download, CopyDocument } from '@element-plus/icons-vue'
import {getValidToken} from "../utils/auth.js";
import request from "../main.js";

// 表单引用
const formRef = ref(null)

// 表单数据
const formData = reactive({
  prompt: '',
  negativePrompt: '',
  samples: 1,
  width: 1024,
  height: 1024,
  steps: 30,
  cfgScale: 7,
  style: ''
})

// 表单验证规则
const rules = {
  prompt: [
    { required: true, message: '请输入提示词', trigger: 'blur' },
    { min: 3, message: '提示词至少需要3个字符', trigger: 'blur' }
  ]
}

// 状态变量
const isLoading = ref(false)
const imageUrl = ref('')
const imageId = ref('')
const errorMessage = ref('')

// 生成图片ID函数
const generateImageId = async () => {
  try {
    const token = getValidToken()
    if (!token) {
      throw new Error('未获取到有效的JWT Token，请重新登录')
    }

    console.log('Token获取成功:', token.substring(0, 10) + '...')

    // 准备请求体数据
    const requestBody = {
      prompt: formData.prompt,
      negativePrompt: formData.negativePrompt,
      samples: formData.samples,
      width: formData.width,
      height: formData.height,
      steps: formData.steps,
      cfgScale: formData.cfgScale,
      style: formData.style
    }

    console.log('请求参数:', requestBody)

    // 发送请求
    const response = await request({
      method: 'post',
      url: '/image/text-to-image',
      data: requestBody
    });

    console.log('响应状态:', response.status)
    console.log('响应数据:', response.data)

    const result = response.data

    // 检查响应结构，注意是code=1成功
    if (result.code !== 1) {
      throw new Error(result.msg || '服务器返回错误码')
    }

    if (!result.data || !result.data.length) {
      throw new Error('响应中缺少有效的数据')
    }

    return result.data[0]
  } catch (error) {
    console.error('生成图片ID出错:', error)

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

    throw error
  }
}

// 获取图片URL的函数
const getImageUrl = async (id) => {
  try {
    // 获取token
    const token = getValidToken()
    if (!token) {
      throw new Error('未登录，请先登录！')
    }

    console.log('获取图片URL，图片ID:', id)

    // 使用axios发送请求
    const response = await request({
      method: 'get',
      url: `/image/get-image/${id}`
    });

    console.log('获取图片URL响应:', response.data)

    const imageResult = response.data
    //注意这里是code等于1成功
    if (imageResult.code !== 1 || !imageResult.data) {
      throw new Error(imageResult.msg || '获取图片链接失败，请稍后重试')
    }

    // 返回图片URL
    return imageResult.data
  } catch (error) {
    console.error('获取图片URL出错:', error)
    throw error
  }
}

// 综合函数：生成图片并获取URL
const generateImage = async () => {
  // 表单验证
  try {
    await formRef.value.validate()
  } catch (error) {
    ElMessage.error('请正确填写表单')
    return
  }

  isLoading.value = true
  imageUrl.value = ''
  errorMessage.value = ''

  try {
    // 第一步：生成图片获取ID
    const id = await generateImageId()
    imageId.value = id

    // 第二步：通过ID获取图片URL
    const url = await getImageUrl(id)
    imageUrl.value = url

    ElNotification({
      title: '生成成功',
      message: '图片已成功生成',
      type: 'success',
      position: 'bottom-right'
    })
  } catch (error) {
    errorMessage.value = error.message || '服务器错误，请稍后重试'
    ElMessage.error(errorMessage.value)
  } finally {
    isLoading.value = false
  }
}

// 下载图片
const downloadImage = () => {
  if (!imageUrl.value) return

  const a = document.createElement('a')
  a.href = imageUrl.value
  a.download = `generated-image-${new Date().getTime()}.png`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)

  ElMessage.success('图片下载已开始')
}

// 复制图片链接
const copyImageUrl = () => {
  if (!imageUrl.value) return

  navigator.clipboard.writeText(imageUrl.value)
      .then(() => {
        ElMessage.success('图片链接已复制到剪贴板')
      })
      .catch(() => {
        ElMessage.error('复制失败，请手动复制')
      })
}

// 页面加载完成后调整容器高度
onMounted(() => {
  // 精简页面高度计算，避免过高
  const adjustPageHeight = () => {
    const container = document.querySelector('.text-to-image-container');
    if (container) {
      // 页面容器本身不设置固定高度，仅由内容撑开
      container.style.height = 'auto';
    }
  };

  adjustPageHeight();
  window.addEventListener('resize', adjustPageHeight);

  return () => {
    window.removeEventListener('resize', adjustPageHeight);
  };
})
</script>

<style scoped>
.text-to-image-container {
  padding: 0 10px 0 10px;
  margin-left: 10px;
  width: 96%;
  display: flex;
  flex-direction: column;
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

/* 结果容器样式 */
.result-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.result-title {
  width: 100%;
  margin-top: 0;
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  text-align: left;
}

/* 图片占位符样式 */
.placeholder-image {
  width: 100%;
  height: 480px; /* 增加高度 */
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

.placeholder-svg {
  width: 120px;
  height: 120px;
  margin-bottom: 16px;
  opacity: 0.7;
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
  height: 480px; /* 增加高度 */
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* 图片包装器 */
.image-wrapper {
  width: 100%;
  position: relative;
}

/* 生成的图片样式 */
.generated-image {
  width: 100%;
  height: 480px; /* 增加高度 */
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
  height: 300px;
}

.image-error .el-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

/* 图片操作按钮 */
.image-actions {
  margin-top: 16px;
  display: flex;
  gap: 15px;
  justify-content: center;
  width: 100%;
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