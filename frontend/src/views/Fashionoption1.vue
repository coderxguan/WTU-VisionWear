<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'
import { Picture, Loading, Download, CopyDocument } from '@element-plus/icons-vue'
import {getValidToken} from "../utils/auth.js";

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

// 修复后的generateImageId函数
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

    // 确保使用正确的基本URL和端口 - 修改为8080
    // 直接使用axios而不是fetch，这样会自动使用main.js中配置的baseURL
    console.log('使用axios发送请求到API')

    const response = await axios({
      method: 'post',
      url: '/user/image/generate', // 不需要指定完整URL，axios会使用baseURL
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
        'token': token
      },
      data: requestBody
    })

    console.log('响应状态:', response.status)
    console.log('响应数据:', response.data)

    // axios已经会帮我们解析JSON，不需要手动解析
    const result = response.data

    // 检查响应结构
    if (result.code !== 0) {
      throw new Error(result.msg || '服务器返回错误码')
    }

    if (!result.data || !result.data.length) {
      throw new Error('响应中缺少有效的数据')
    }

    return result.data[0]
  } catch (error) {
    console.error('生成图片ID出错:', error)

    // 增强错误日志，显示更多请求细节
    if (error.response) {
      // 服务器响应了，但状态码不是2xx
      console.error('服务器响应:', {
        status: error.response.status,
        headers: error.response.headers,
        data: error.response.data
      })
    } else if (error.request) {
      // 请求已发送，但没有收到响应
      console.error('请求已发送但没有响应:', error.request)
    }

    throw error
  }
}

// 修改获取图片URL的函数
const getImageUrl = async (id) => {
  try {
    // 获取token
    const token = getValidToken()
    if (!token) {
      throw new Error('未登录，请先登录！')
    }

    console.log('获取图片URL，图片ID:', id)

    // 使用axios发送请求
    const response = await axios({
      method: 'get',
      url: `/user/images/${id}`,
      headers: {
        'Authorization': `Bearer ${token}`,
        'token': token
      }
    })

    console.log('获取图片URL响应:', response.data)

    const imageResult = response.data

    if (imageResult.code !== 0 || !imageResult.data) {
      throw new Error(imageResult.msg || '获取图片链接失败，请稍后重试')
    }

    // 返回图片URL
    return imageResult.data
  } catch (error) {
    console.error('获取图片URL出错:', error)
    throw error
  }
}

// 引入axios (确保在script setup部分顶部添加这一行)
import axios from 'axios'

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
</script>

<template>
  <div class="text-to-image-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>AI 文生图</h2>
        </div>
      </template>

      <el-row :gutter="30">
        <!-- 输入表单 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <el-form :model="formData" label-position="top" :rules="rules" ref="formRef">
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
                    <el-option label="写实风格" value="realistic"></el-option>
                    <el-option label="动漫风格" value="anime"></el-option>
                    <el-option label="艺术风格" value="artistic"></el-option>
                    <el-option label="3D渲染" value="3d"></el-option>
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
          <div class="result-wrapper">
            <el-empty v-if="!isLoading && !imageUrl && !errorMessage" description="填写左侧表单并点击'生成图片'开始创作"></el-empty>

            <div v-if="isLoading" class="loading-container">
              <el-skeleton style="width: 100%" animated>
                <template #template>
                  <el-skeleton-item variant="image" style="width: 100%; height: 400px" />
                </template>
              </el-skeleton>
              <div class="loading-text">
                <el-icon class="is-loading"><Loading /></el-icon>
                <span>正在生成您的图片，请稍候...</span>
              </div>
            </div>

            <div v-if="errorMessage" class="error-container">
              <el-alert
                  :title="errorMessage"
                  type="error"
                  show-icon
                  :closable="false"
              ></el-alert>
            </div>

            <div v-if="!isLoading && imageUrl" class="image-container">
              <h3>生成结果</h3>
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

              <div class="image-actions">
                <el-button type="primary" @click="downloadImage" :icon="Download">
                  下载图片
                </el-button>
                <el-button type="info" @click="copyImageUrl" :icon="CopyDocument">
                  复制链接
                </el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<style scoped>
.text-to-image-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 15px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 1.8rem;
  color: var(--el-text-color-primary);
}

.result-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 400px;
}

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

.error-container {
  margin-top: 20px;
}

.image-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.image-container h3 {
  margin-bottom: 20px;
  color: var(--el-text-color-primary);
}

.generated-image {
  width: 100%;
  max-height: 500px;
  border-radius: 6px;
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

.image-actions {
  margin-top: 20px;
  display: flex;
  gap: 15px;
  justify-content: center;
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