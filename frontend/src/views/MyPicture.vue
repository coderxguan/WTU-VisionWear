<template>
    <a href="#" class="mine" @click="showMyPicture=true">我的图片</a>
    <div class="MyPictures" v-show="showMyPicture">
        <el-button type="plain" class="CloseMyPicture" @click="showMyPicture=false"><span class="iconfont icon-fangxiang-xiangshang"></span></el-button>
        <img class="showPicture" v-for="(item,index) in images.slice(mageIndex,mageIndex+10)" :src="item">
        <el-button type="plain" class="next" style="width: 10px;" @click="mageIndex=mageIndex<images.length-10?mageIndex+1:images.length-10"><span class="iconfont icon-fangxiang-xiangyou"></span></el-button>
        <el-button type="plain" class="pre" style="width: 10px;" @click="mageIndex=mageIndex>=1?mageIndex-1:0"><span class="iconfont icon-fangxiang-xiangzuo"></span></el-button>
    </div>
</template>

<script setup>
import { onMounted,ref } from 'vue';
import {getValidToken} from "../utils/auth.js";
import request from "../main.js";
import '../styles/download/font/iconfont.css'
const showMyPicture = ref(false);
const images = ref([]);
const mageIndex = ref(0);
    // 获取图片URL的函数
const getAllImage = async () => {
    try {
    // 获取token
        const token = getValidToken()
        if (!token) {
        throw new Error('未登录，请先登录！')
        }

        // 使用axios发送请求
        const response = await request({
            method: 'get',
            url: `/user/getAllImage`
        });
        const imageResult = response.data
        console.log('获取图片URL响应:', response.data)

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

onMounted(async () => {
    images.value = await getAllImage()
})


</script>

<style scoped>
.mine {
    display: left;
    color: white;
    text-decoration: none;
    margin-right: 350px;
    font-size: 15px;
}
.CloseMyPicture {
  float: right;
  height: 100%;
}
.MyPictures {
  position: absolute;
  top: 0;
  left: 260px;
  width: 1250px;
  height: 150px;
  overflow: hidden;
  text-align: center;
  background-color: #F9F9F9;
  z-index: 9999;
  border: 1px solid #fafcf1;
}
.showPicture {
    display: block;
    float: left;
    object-fit: cover;
    border: 1px solid rgb(190, 242, 181);
    margin: 1.5px;
    width: 8.5%;
    height: 95%;
}
.pre {
    /* wight: 20px; */
    float: right;
    height: 100%;
    margin: 0;
}
.next {
    /* wight: 20px; */
    float: right;
    height: 100%;
    margin: 0;
}
</style>