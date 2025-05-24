<script setup>
import drag from './drag.vue';
import { ref,onMounted } from 'vue'
import {getValidToken} from "../utils/auth.js";
import request from "../main.js";
const textFeature = ref('')
const check = ref(null)
const showUrl = ref('')
const sketchUrl = ref('')
const referenceUrl = ref('')
const resUml = ref('')

const changeShow = (url, style) => {
  if (style === 'update') {
    if (showUrl.value === url) {
      // 如果相同，先清空再赋值（强制更新）
      showUrl.value = ''
      setTimeout(() => {
        showUrl.value = url
      }, 0)
    } else {
      showUrl.value = url
    }
  } else {
    if (showUrl.value === url) {
      showUrl.value = ''
    }
  }
}

const sketchChange = (url, style) => {
  sketchUrl.value = url
  changeShow(url, style)
}

const referenceChange = (url, style) => {
  referenceUrl.value = url
  changeShow(url, style)
}

const changePicture = (url) => {
  showUrl.value = url
}





const generateImage = async () => {
  try {
    const token = getValidToken()
    if (!token) {
      throw new Error('未获取到有效的JWT Token，请重新登录')
    }

    // 准备请求体数据
    const requestBody = {
      "imageUrlList": [sketchUrl.value, referenceUrl.value],
      "dimensions": "",
      "mode": "",
      "hookUrl": ""
    }

    // 发送请求
    const response = await request({
      method: 'post',
      url: '/image/image-fusion',
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

    return result.data
  } catch (error) {
    console.error('生成图片出错:', error)

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

const generate = ()=>{
  const resData = generateImage()
}

</script>

<template>
    <div class="container">
      <el-container>
        <el-aside width="200px" class="styleFusionMenu">
          <ul class="dataList">
            <li class="sketch">
              <drag @update="sketchChange" @click="changePicture">款式图</drag>
            </li>
  
            <li class="reference">
              <drag @update="referenceChange" @click="changePicture">参考图</drag>
            </li>
          </ul>

          <div class="designFeature">
            <p>设计特征</p>
            <el-input
              v-model="textFeature"
              style="width: 345px"
              :autosize="{ minRows: 5, maxRows: 5 }"
              type="textarea"
              placeholder="请输入对设计的描述或描述提示词提高生成准度"
            />
          </div>
          
          <div class="create" @click="generate">
            <el-button type="success" plain style="width: 100%">一键生成</el-button>
          </div>
        </el-aside>
  
        <el-main class="show">
          <div class="check" ref="check">
            <img v-if="showUrl" :src="showUrl" style="width: 100%; height: 100%; object-fit: contain;" />
          </div>
          <div class="result">

          </div>
        </el-main>
        
      </el-container>      
    </div>
  </template>

<style scoped>
.container {
  padding: 0 10px 0 10px;
  margin-left: 10px;
  width: 96%;
  display: flex;
  flex-direction: column;
}

.styleFusionMenu {
  width: 350px;
  height: 100%;
  border-radius: 10px;
}

.dataList {
  list-style-type: none;
  margin: 0;
  padding: 0;
}
.showPicture {
  width: 100%;
  height: 150px;
}
.showPicture img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
.create {
  padding: 20px 0;
}
.show {
  display: flex;
  border-radius: 10px;
  height: 700px;
}
.show .check {
  width: 50%;
  height: 100%;
  border: dashed 1px green;
  border-radius: 10px;
  box-sizing: border-box;
}
.show .result {
  width: 50%;
  height: 100%;
  border: dashed 1px green;
  border-radius: 10px;
  box-sizing: border-box;
}
</style>