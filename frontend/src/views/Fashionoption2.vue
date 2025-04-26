<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const textFeature = ref('')
const sketchFileList = ref([])
const referenceFileList = ref([])
const sketchPreviewUrl = ref('')
const referencePreviewUrl = ref('')

const handleSketchChange = (file, fileList) => {
    sketchFileList.value = fileList.slice(-1)
    if (sketchFileList.value.length > 0 && sketchFileList.value[0].raw) {
      sketchPreviewUrl.value = URL.createObjectURL(sketchFileList.value[0].raw)
    }
}

const handleReferenceChange = (file, fileList) => {
  referenceFileList.value = fileList.slice(-1)
    if (referenceFileList.value.length > 0 && referenceFileList.value[0].raw) {
      referencePreviewUrl.value = URL.createObjectURL(referenceFileList.value[0].raw)
    }
}

// 删除图片
const handleRemove = (file, fileList, type) => {
  if (type === 'sketch') {
    sketchFileList.value = []
  } else if (type === 'reference') {
    referenceFileList.value = []
  }
}
//右侧第一个格子展示图片
const HandlePreview = (file) => {
  
}
// 第二个格子展示result图片
</script>

<template>
    <div class="container">
      <el-container>
        <el-aside width="200px" class="styleFusionMenu">
          <ul class="dataList">
            <li class="sketch">
              <p>款式图*<el-button type="round" @click="handleRemove(file, fileList, 'sketch')">清空</el-button></p>
              <div class="showPicture" v-if="sketchFileList.length > 0 && sketchFileList[0].raw" @click="HandlePreview(sketchFileList[0])">
                <img :src= "sketchPreviewUrl"/>
              </div>
              <el-upload v-if="sketchFileList.length < 1"
                class="upload-sketch"
                drag
                show-file-list=false
                :auto-upload="false"
                :file-list="sketchFileList"
                :on-change="handleSketchChange"
                :on-exceed="handleExceed"
                :on-preview="handlePreview"
                accept=".jpg,.jpeg,.png"
              >
              点击或拖动以上传
              </el-upload>
            </li>
  
            <li class="reference">
              <p>参考图*<el-button type="round" @click="handleRemove(file, fileList, 'reference')">清空</el-button></p>
              <div class="showPicture" v-if="referenceFileList.length > 0 && referenceFileList[0].raw" @click="HandlePreview(referenceFileList[0])">
                <img :src= "referencePreviewUrl"/>
              </div>
              <el-upload v-if="referenceFileList.length < 1"
                class="upload-reference"
                drag
                :auto-upload="false"
                :file-list="referenceFileList"
                :on-change="handleReferenceChange"
                :on-exceed="handleExceed"
                :on-preview="handlePreview"
                accept=".jpg,.jpeg,.png"
              >
              点击或拖动以上传
              </el-upload>
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
          <div class="create">
            <el-button type="success" plain style="width: 100%">一键生成</el-button>
          </div>
        </el-aside>
  
        <el-main class="show">
          <div class="check">

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