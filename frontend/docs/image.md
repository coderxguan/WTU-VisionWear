# Image 模块

image 模块提供图像处理相关的 API 函数，包括上传、文本生成图像和图像风格转换等功能。

## 导入方式

```js
import { uploadImage, textToImage, imageToImage } from '@/api/modules/image';
```

## API 函数

### uploadImage(file, onUploadProgress)

上传图像文件。

**参数:**
- `file`: 文件对象，通常来自 input 元素或拖放
- `onUploadProgress`: 可选，上传进度回调函数，接收一个包含上传百分比的事件对象

**返回:**
- Promise 对象，解析为上传结果，包含图像 URL 等信息

**示例:**
```js
// 从文件输入框中获取文件
const fileInput = document.getElementById('fileInput');
const file = fileInput.files[0];

// 调用上传函数
uploadImage(file, progressEvent => {
  const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
  console.log(`上传进度: ${percentCompleted}%`);
})
  .then(res => {
    console.log('图像上传成功:', res.data.imageUrl);
  })
  .catch(error => {
    console.error('图像上传失败:', error.message);
  });
```

### textToImage(textPrompt, options)

根据文本提示生成图像（文生图）。

**参数:**
- `textPrompt`: 文本描述，用于生成图像
- `options`: 可选的配置项
  - `width`: 图像宽度，默认 512
  - `height`: 图像高度，默认 512
  - `steps`: 生成步数，默认 50
  - 其他 Stable Diffusion 参数

**返回:**
- Promise 对象，解析为包含生成图像 URL 的结果

**示例:**
```js
const prompt = '一只可爱的小猫在阳光下玩耍';
const options = {
  width: 768,
  height: 512,
  steps: 30
};

textToImage(prompt, options)
  .then(res => {
    console.log('图像生成成功:', res.data.imageUrl);
  })
  .catch(error => {
    console.error('图像生成失败:', error.message);
  });
```

### imageToImage(sourceImage, textPrompt, options)

基于源图像生成新图像（图生图）。

**参数:**
- `sourceImage`: 源图像文件对象
- `textPrompt`: 文本描述，引导生成方向
- `options`: 可选的配置项
  - `strength`: 变化强度 (0-1)，默认 0.7
  - `width`: 输出图像宽度（将自动调整到支持的尺寸）
  - `height`: 输出图像高度（将自动调整到支持的尺寸）

**返回:**
- Promise 对象，解析为包含生成图像 URL 的结果

**示例:**
```js
const sourceFile = document.getElementById('imageInput').files[0];
const prompt = '将这张照片转换为梵高风格的油画';
const options = {
  strength: 0.8,
  width: 512,
  height: 512
};

imageToImage(sourceFile, prompt, options)
  .then(res => {
    console.log('图像转换成功:', res.data.imageUrl);
  })
  .catch(error => {
    console.error('图像转换失败:', error.message);
  });
```

## 注意事项

- 上传图像时，请确保文件大小适中，过大的文件可能导致上传失败
- 生成图像的尺寸将自动调整为模型支持的尺寸（如 512x512, 768x512 等）
- 处理较大图像或复杂提示词时，请预期较长的处理时间 