package com.wtu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wtu.DTO.ImageToImageDTO;
import com.wtu.DTO.TextToImageDTO;
import com.wtu.VO.ImageToImageVO;
import com.wtu.VO.TextToImageVO;
import com.wtu.config.StableDiffusionConfig;
import com.wtu.entity.Image;
import com.wtu.mapper.ImageMapper;
import com.wtu.service.ImageService;
import com.wtu.service.ImageStorageService;
import com.wtu.utils.TransApi;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    // IOC 注入
    private final RestTemplate restTemplate;
    private final StableDiffusionConfig config;
    private final ObjectMapper objectMapper;
    private final ImageStorageService imageStorageService;
    private final ImageMapper imageMapper;

    // 百度翻译API
    private TransApi transApi;

    @Value("${vision.translate.appid}")
    private String appId;

    @Value("${vision.translate.security-key}")
    private String securityKey;

    // 初始化翻译API
    @PostConstruct
    public void init() {
        this.transApi = new TransApi(appId, securityKey);
    }

    // 提取通用的翻译方法
    private void translatePrompts(Object requestObj) {
        if (requestObj instanceof TextToImageDTO textRequest) {
            textRequest.setPrompt(translateToEnglish(textRequest.getPrompt()));
            if (textRequest.getNegativePrompt() != null && !textRequest.getNegativePrompt().isEmpty()) {
                textRequest.setNegativePrompt(translateToEnglish(textRequest.getNegativePrompt()));
            }
        } else if (requestObj instanceof ImageToImageDTO imageRequest) {
            imageRequest.setPrompt(translateToEnglish(imageRequest.getPrompt()));
            if (imageRequest.getNegativePrompt() != null && !imageRequest.getNegativePrompt().isEmpty()) {
                imageRequest.setNegativePrompt(translateToEnglish(imageRequest.getNegativePrompt()));
            }
        }
    }

    // 提取翻译方法
    private String translateToEnglish(String text) {
        try {
            String result = transApi.getTransResult(text, "zh", "en");
            JsonNode jsonNode = objectMapper.readTree(result);
            JsonNode transResult = jsonNode.path("trans_result");
            if (transResult.isArray() && transResult.size() > 0) {
                return transResult.get(0).path("dst").asText(text);
            }
            return text;
        } catch (Exception e) {
            log.error("翻译失败，使用原文本", e);
            return text;
        }
    }


    // 文本生成图像
    @Override
    public TextToImageVO textToImage(TextToImageDTO request, Long userId) throws Exception {
        // 翻译提示词
        translatePrompts(request);

        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        log.info("开始图像生成请求: {}, 提示: {}", requestId, request.getPrompt());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.set("Authorization", "Bearer " + config.getApiKey());

            ObjectNode requestBody = createTextToImageRequestBody(request);
            HttpEntity<String> httpEntity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

            String modelEndpoint = "/generation/stable-diffusion-xl-1024-v1-0/text-to-image";
            String url = config.getBaseUrl() + modelEndpoint;

            String responseBody = restTemplate.postForObject(url, httpEntity, String.class);
            JsonNode responseJson = objectMapper.readTree(responseBody);
            List<TextToImageVO.GeneratedImage> generatedImages = parseResponse(responseJson, userId, TextToImageVO.GeneratedImage.class);

            long duration = System.currentTimeMillis() - startTime;
            log.info("图像生成完成: {}, 耗时: {}ms", requestId, duration);

            return TextToImageVO.builder()
                    .requestId(requestId)
                    .images(generatedImages)
                    .prompt(request.getPrompt())
                    .generationTimeMs(duration)
                    .build();

        } catch (HttpStatusCodeException e) {
            log.error("API请求失败: {}, 状态码: {}", requestId, e.getStatusCode());
            throw new Exception("调用 Stable Diffusion API 失败: " + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            log.error("REST客户端异常: {}", requestId, e);
            throw new Exception("无法连接到Stable Diffusion API服务", e);
        } catch (Exception e) {
            log.error("图像生成过程中发生未预期的错误: {}", requestId, e);
            throw new Exception("图像生成失败: " + e.getMessage(), e);
        }
    }

    // 图像生成图像
    @Override
    public ImageToImageVO imageToImage(ImageToImageDTO request, Long userId) throws Exception {
        // 翻译提示词
        translatePrompts(request);

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

            // 准备请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Accept", "application/json");
            headers.set("Authorization", "Bearer " + config.getApiKey());

            // 准备请求体
            MultiValueMap<String, Object> body = createImageToImageMultipartBody(request, imageBytes);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 创建带有更长超时时间的RestTemplate
            RestTemplate timeoutRestTemplate = createTimeoutRestTemplate();

            String modelEndpoint = "/generation/stable-diffusion-xl-1024-v1-0/image-to-image";
            String url = config.getBaseUrl() + modelEndpoint;

            // 发送请求并获取响应
            String responseBody = timeoutRestTemplate.postForObject(url, requestEntity, String.class);
            JsonNode responseJson = objectMapper.readTree(responseBody);
            List<ImageToImageVO.GeneratedImage> generatedImages = parseResponse(responseJson, userId, ImageToImageVO.GeneratedImage.class);

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
            log.error("API请求失败: {}, 状态码: {}", requestId, e.getStatusCode());
            throw new Exception("调用 Stable Diffusion API 失败: " + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            log.error("REST客户端异常: {}", requestId, e);
            throw new Exception("无法连接到Stability AI API服务", e);
        } catch (Exception e) {
            log.error("以图生图过程中发生未预期的错误: {}", requestId, e);
            throw new Exception("以图生图失败: " + e.getMessage(), e);
        }
    }

    // 创建一个带有超时设置的RestTemplate
    private RestTemplate createTimeoutRestTemplate() {
        RestTemplate timeoutRestTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(60000);  // 60秒连接超时
        requestFactory.setReadTimeout(120000);    // 120秒读取超时
        timeoutRestTemplate.setRequestFactory(requestFactory);
        return timeoutRestTemplate;
    }

    // 创建图像到图像的请求体
    private MultiValueMap<String, Object> createImageToImageMultipartBody(ImageToImageDTO request, byte[] imageBytes) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // 添加初始图像
        ByteArrayResource imageResource = new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return "image.png";
            }
        };
        body.add("init_image", imageResource);

        // 添加提示词
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
        body.add("seed", request.getSeed() != null ? request.getSeed() : 0);
        body.add("sampler", "K_DPMPP_2M");

        // 设置样式（如果有）
        if (request.getStyle() != null && !request.getStyle().isEmpty()) {
            body.add("style_preset", request.getStyle());
        }

        return body;
    }

    // 获取用户所有图像URL
    @Override
    public List<String> getAllImageUrls(Long userId) {
        LambdaQueryWrapper<Image> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Image::getUserId, userId)
                .eq(Image::getStatus, 0)
                .orderByDesc(Image::getCreateTime);

        List<String> imageUrls = imageMapper.selectList(wrapper).stream()
                .map(Image::getImageUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("获取到用户 {} 的图像URL: {} 个", userId, imageUrls.size());
        return imageUrls;
    }

    // 创建文本到图像的请求体
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
        requestBody.put("sampler", "K_DPMPP_2M");

        if (request.getStyle() != null && !request.getStyle().isEmpty()) {
            requestBody.put("style_preset", request.getStyle());
        }

        return requestBody;
    }

    // 通用响应解析方法，使用泛型来处理不同的VO类型
    private <T> List<T> parseResponse(JsonNode responseJson, Long userId, Class<T> clazz) {
        List<T> images = new ArrayList<>();
        JsonNode artifacts = responseJson.path("artifacts");

        if (artifacts.isArray()) {
            for (JsonNode artifact : artifacts) {
                if (!artifact.has("base64")) {
                    log.warn("响应中缺少base64字段");
                    continue;
                }

                String base64Image = artifact.path("base64").asText();
                String imageId = imageStorageService.saveBase64Image(base64Image, userId);
                String imageUrl = imageStorageService.getImageUrl(imageId);

                int width = artifact.has("width") ? artifact.path("width").asInt() : 1024;
                int height = artifact.has("height") ? artifact.path("height").asInt() : 1024;
                long seed = artifact.has("seed") ? artifact.path("seed").asLong() : 0;

                // 根据类型创建不同的VO对象
                try {
                    Object imageObj;
                    if (clazz == TextToImageVO.GeneratedImage.class) {
                        imageObj = TextToImageVO.GeneratedImage.builder()
                                .imageId(imageId)
                                .imageUrl(imageUrl)
                                .width(width)
                                .height(height)
                                .seed(seed)
                                .build();
                    } else {
                        imageObj = ImageToImageVO.GeneratedImage.builder()
                                .imageId(imageId)
                                .imageUrl(imageUrl)
                                .width(width)
                                .height(height)
                                .seed(seed)
                                .build();
                    }
                    images.add((T) imageObj);
                } catch (ClassCastException e) {
                    log.error("类型转换失败", e);
                }
            }
        } else {
            log.warn("API响应中未找到artifacts数组或格式不正确");
        }

        return images;
    }
}