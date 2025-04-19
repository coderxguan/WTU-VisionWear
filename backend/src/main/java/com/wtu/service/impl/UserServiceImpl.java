package com.wtu.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wtu.DTO.ImageToImageDTO;
import com.wtu.DTO.TextToImageDTO;
import com.wtu.VO.ImageToImageVO;
import com.wtu.VO.TextToImageVO;
import com.wtu.config.StableDiffusionConfig;
import com.wtu.service.ImageStorageService;
import com.wtu.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final RestTemplate restTemplate;
    private final StableDiffusionConfig config;
    private final ObjectMapper objectMapper;
    private final ImageStorageService imageStorageService;

    /**
     * 根据提示生成图像
     * @param request 图像生成请求
     * @param userId 用户ID
     * @return 图像生成响应
     */
    @Override
    public TextToImageVO textToImage(TextToImageDTO request, Long userId) throws Exception {
        // 保持原来的textToImage方法实现不变
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        log.info("开始图像生成请求: {}, 提示: {}", requestId, request.getPrompt());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // 添加正确的 Accept 头
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.set("Authorization", "Bearer " + config.getApiKey());

            // 创建请求体
            ObjectNode requestBody = createTextToImageRequestBody(request);

            // 记录请求体，帮助调试
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            log.debug("发送到API的请求体: {}", requestBodyJson);

            HttpEntity<String> httpEntity =
                    new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

            // 使用原始的模型端点
            String modelEndpoint = "/generation/stable-diffusion-xl-1024-v1-0/text-to-image";
            String url = config.getBaseUrl() + modelEndpoint;

            log.debug("发送请求到: {}", url);
            String responseBody = restTemplate.postForObject(url, httpEntity, String.class);

            // 解析响应
            JsonNode responseJson = objectMapper.readTree(responseBody);
            List<TextToImageVO.GeneratedImage> generatedImages = parseTextToImageResponse(responseJson, userId);

            long duration = System.currentTimeMillis() - startTime;
            log.info("图像生成完成: {}, 耗时: {}ms", requestId, duration);

            return TextToImageVO.builder()
                    .requestId(requestId)
                    .images(generatedImages)
                    .prompt(request.getPrompt())
                    .generationTimeMs(duration)
                    .build();

        } catch (HttpStatusCodeException e) {
            log.error("API请求失败: {}, 状态码: {}, 响应: {}", requestId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new Exception("调用 Stable Diffusion API 失败: " + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            log.error("REST客户端异常: {}", requestId, e);
            throw new Exception("无法连接到Stable Diffusion API服务", e);
        } catch (Exception e) {
            log.error("图像生成过程中发生未预期的错误: {}", requestId, e);
            throw new Exception("图像生成失败: " + e.getMessage(), e);
        }
    }

    @Override
    public ImageToImageVO imageToImage(ImageToImageDTO request, Long userId) throws Exception {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        log.info("开始以图生图请求: {}, 源图像ID: {}, 提示: {}", requestId, request.getSourceImageId(), request.getPrompt());

        try {
            // 获取源图像
            String sourceImageUrl = imageStorageService.getImageUrl(request.getSourceImageId());
            byte[] imageBytes = restTemplate.getForObject(sourceImageUrl, byte[].class);
            if (imageBytes == null) {
                throw new Exception("无法获取源图像数据");
            }

            // 使用multipart/form-data格式发送请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Accept", "application/json");
            headers.set("Authorization", "Bearer " + config.getApiKey());

            // 创建multipart请求体
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // 添加初始图像
            ByteArrayResource imageResource = new ByteArrayResource(imageBytes) {
                @Override
                public String getFilename() {
                    return "image.png";
                }
            };
            body.add("init_image", imageResource);

            // 使用正确的格式添加text_prompts参数
            body.add("text_prompts[0][text]", request.getPrompt());
            body.add("text_prompts[0][weight]", "1.0");

            // 添加负面提示（如果有）
            if (request.getNegativePrompt() != null && !request.getNegativePrompt().isEmpty()) {
                body.add("text_prompts[1][text]", request.getNegativePrompt());
                body.add("text_prompts[1][weight]", "-1.0");
            }

            // 添加其他参数
            body.add("init_image_mode", "IMAGE_STRENGTH");
            body.add("image_strength", request.getImageStrength());
            body.add("cfg_scale", request.getCfgScale());
            body.add("samples", request.getSamples());
            body.add("steps", request.getSteps());

            // 设置种子
            if (request.getSeed() != null) {
                body.add("seed", request.getSeed());
            } else {
                body.add("seed", 0);
            }

            // 设置样式（如果有）
            if (request.getStyle() != null && !request.getStyle().isEmpty()) {
                body.add("style_preset", request.getStyle());
            }

            // 设置采样器
            body.add("sampler", "K_DPMPP_2M");

            // 创建HTTP实体
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 创建带有更长超时时间的RestTemplate
            RestTemplate timeoutRestTemplate = new RestTemplate();
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setConnectTimeout(60000);  // 60秒连接超时
            requestFactory.setReadTimeout(120000);    // 120秒读取超时
            timeoutRestTemplate.setRequestFactory(requestFactory);

            // 使用模型端点
            String modelEndpoint = "/generation/stable-diffusion-xl-1024-v1-0/image-to-image";
            String url = config.getBaseUrl() + modelEndpoint;

            log.debug("发送请求到: {}", url);
            log.debug("请求参数: prompt={}, imageStrength={}, samples={}, steps={}",
                    request.getPrompt(), request.getImageStrength(),
                    request.getSamples(), request.getSteps());

            // 发送请求并获取响应
            String responseBody = timeoutRestTemplate.postForObject(url, requestEntity, String.class);

            // 解析响应
            JsonNode responseJson = objectMapper.readTree(responseBody);
            List<ImageToImageVO.GeneratedImage> generatedImages = parseImageToImageResponse(responseJson, userId);

            long duration = System.currentTimeMillis() - startTime;
            log.info("以图生图完成: {}, 耗时: {}ms", requestId, duration);

            return ImageToImageVO.builder()
                    .requestId(requestId)
                    .images(generatedImages)
                    .sourceImageId(request.getSourceImageId())
                    .prompt(request.getPrompt())
                    .generationTimeMs(duration)
                    .build();

        } catch (HttpStatusCodeException e) {
            log.error("API请求失败: {}, 状态码: {}, 响应: {}", requestId, e.getStatusCode(), e.getResponseBodyAsString());
            log.error("请求详情: URL={}, 源图像ID={}, 提示词={}",
                    config.getBaseUrl() + "/generation/stable-diffusion-xl-1024-v1-0/image-to-image",
                    request.getSourceImageId(),
                    request.getPrompt());
            throw new Exception("调用 Stable Diffusion API 失败: " + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            log.error("REST客户端异常: {}", requestId, e);
            throw new Exception("无法连接到Stability AI API服务", e);
        } catch (Exception e) {
            log.error("以图生图过程中发生未预期的错误: {}", requestId, e);
            throw new Exception("以图生图失败: " + e.getMessage(), e);
        }
    }
    // 保持原来的createTextToImageRequestBody方法不变
    private ObjectNode createTextToImageRequestBody(TextToImageDTO request) {
        ObjectNode requestBody = objectMapper.createObjectNode();

        // 添加文本提示
        ArrayNode textPrompts = objectMapper.createArrayNode();
        ObjectNode positivePrompt = objectMapper.createObjectNode();
        positivePrompt.put("text", request.getPrompt());
        positivePrompt.put("weight", 1.0);
        textPrompts.add(positivePrompt);

        // 添加负面提示（如果有）
        if (request.getNegativePrompt() != null && !request.getNegativePrompt().isEmpty()) {
            ObjectNode negativePrompt = objectMapper.createObjectNode();
            negativePrompt.put("text", request.getNegativePrompt());
            negativePrompt.put("weight", -1.0);
            textPrompts.add(negativePrompt);
        }

        requestBody.set("text_prompts", textPrompts);
        requestBody.put("cfg_scale", request.getCfgScale());
        requestBody.put("height", request.getHeight());
        requestBody.put("width", request.getWidth());
        requestBody.put("samples", request.getSamples());
        requestBody.put("steps", request.getSteps());

        // 设置采样器
        requestBody.put("sampler", "K_DPMPP_2M");

        // 添加样式预设（如果支持）
        if (request.getStyle() != null && !request.getStyle().isEmpty()) {
            requestBody.put("style_preset", request.getStyle());
        }

        return requestBody;
    }

    // 移除原来的createImageToImageRequestBody方法，因为我们不再使用JSON格式

    // 保持原来的parseTextToImageResponse方法不变
    private List<TextToImageVO.GeneratedImage> parseTextToImageResponse(JsonNode responseJson, Long userId) {
        List<TextToImageVO.GeneratedImage> images = new ArrayList<>();

        // 记录完整响应以便调试
        log.debug("API响应: {}", responseJson.toString());

        JsonNode artifacts = responseJson.path("artifacts");
        if (artifacts.isArray()) {
            for (JsonNode artifact : artifacts) {
                String base64Image = artifact.path("base64").asText();

                // 保存图像到OSS并获取ID
                String imageId = imageStorageService.saveBase64Image(base64Image, userId);

                // 获取OSS访问URL
                String imageUrl = imageStorageService.getImageUrl(imageId);

                // 从响应中获取图像属性，如果不存在则使用默认值
                int width = artifact.has("width") ? artifact.path("width").asInt() : 1024;
                int height = artifact.has("height") ? artifact.path("height").asInt() : 1024;
                long seed = artifact.has("seed") ? artifact.path("seed").asLong() : 0;

                // 创建生成的图像对象
                TextToImageVO.GeneratedImage image = TextToImageVO.GeneratedImage.builder()
                        .imageId(imageId)
                        .imageUrl(imageUrl)
                        .width(width)
                        .height(height)
                        .seed(seed)
                        .build();

                images.add(image);
            }
        } else {
            log.warn("API响应中未找到artifacts数组或格式不正确: {}", responseJson);
        }

        return images;
    }

    // 保持原来的parseImageToImageResponse方法不变
    private List<ImageToImageVO.GeneratedImage> parseImageToImageResponse(JsonNode responseJson, Long userId) {
        List<ImageToImageVO.GeneratedImage> images = new ArrayList<>();

        // 记录完整响应以便调试
        log.debug("API响应: {}", responseJson.toString());

        // 在API版本中，生成的图像在"artifacts"数组中
        JsonNode artifacts = responseJson.path("artifacts");
        if (artifacts.isArray()) {
            for (JsonNode artifact : artifacts) {
                // 确认base64字段存在
                if (!artifact.has("base64")) {
                    log.warn("响应中缺少base64字段: {}", artifact.toString());
                    continue;
                }

                String base64Image = artifact.path("base64").asText();
                log.debug("获取到base64图像，长度: {}", base64Image.length());

                // 保存图像到OSS并获取ID
                String imageId = imageStorageService.saveBase64Image(base64Image, userId);
                log.debug("图像已保存到OSS，imageId: {}", imageId);

                // 获取OSS访问URL
                String imageUrl = imageStorageService.getImageUrl(imageId);

                // 从响应中获取图像属性，如果不存在则使用默认值
                int width = artifact.has("width") ? artifact.path("width").asInt() : 1024;
                int height = artifact.has("height") ? artifact.path("height").asInt() : 1024;
                long seed = artifact.has("seed") ? artifact.path("seed").asLong() : 0;

                // 创建生成的图像对象
                ImageToImageVO.GeneratedImage image = ImageToImageVO.GeneratedImage.builder()
                        .imageId(imageId)
                        .imageUrl(imageUrl)
                        .width(width)
                        .height(height)
                        .seed(seed)
                        .build();

                images.add(image);
                log.debug("已处理生成的图像: {}", image);
            }
        } else {
            log.warn("API响应中未找到artifacts数组或格式不正确: {}", responseJson);
        }

        return images;
    }
}