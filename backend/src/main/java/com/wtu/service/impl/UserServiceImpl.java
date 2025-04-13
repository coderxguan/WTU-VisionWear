package com.wtu.service.impl;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wtu.DTO.ImageGenerationRequest;
import com.wtu.DTO.ImageGenerationResponse;
import com.wtu.config.StableDiffusionConfig;
import com.wtu.exception.ServiceException;
import com.wtu.service.ImageStorageService;
import com.wtu.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
     * @param  *图像生成请求
     * @return 图像生成响应
     */
    @Override
    public ImageGenerationResponse textToimage(ImageGenerationRequest request) throws Exception {
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
            ObjectNode requestBody = createRequestBody(request);

            // 记录请求体，帮助调试
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            log.debug("发送到API的请求体: {}", requestBodyJson);


            HttpEntity<String> httpEntity =
                    new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

            // 确定要使用的模型
            String modelEndpoint = "/generation/stable-diffusion-xl-1024-v1-0/text-to-image";
            String url = config.getBaseUrl() + modelEndpoint;

            log.debug("发送请求到: {}", url);
            String responseBody = restTemplate.postForObject(url, httpEntity, String.class);

            // 解析响应
            JsonNode responseJson = objectMapper.readTree(responseBody);
            List<ImageGenerationResponse.GeneratedImage> generatedImages = parseResponse(responseJson);

            long duration = System.currentTimeMillis() - startTime;
            log.info("图像生成完成: {}, 耗时: {}ms", requestId, duration);

            return ImageGenerationResponse.builder()
                    .requestId(requestId)
                    .images(generatedImages)
                    .prompt(request.getPrompt())
                    .generationTimeMs(duration)
                    .build();

        } catch (HttpStatusCodeException e) {
            log.error("API请求失败: {}, 状态码: {}, 响应: {}", requestId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new Exception(e);
        } catch (RestClientException e) {
            log.error("REST客户端异常: {}", requestId, e);
            throw new Exception("无法连接到Stable Diffusion API服务");
        } catch (Exception e) {
            log.error("图像生成过程中发生未预期的错误: {}", requestId, e);
            throw  new Exception("图像生成失败: " + e.getMessage());
        }

    }

    private ObjectNode createRequestBody(ImageGenerationRequest request) {
        ObjectNode requestBody = objectMapper.createObjectNode();

        // 添加文本提示
        ArrayNode textPrompts = objectMapper.createArrayNode();
        ObjectNode positivePrompt = objectMapper.createObjectNode();
        positivePrompt.put("text", request.getPrompt());
        positivePrompt.put("weight", 1);
        textPrompts.add(positivePrompt);

        // 添加负面提示（如果有）
        if (request.getNegativePrompt() != null && !request.getNegativePrompt().isEmpty()) {
            ObjectNode negativePrompt = objectMapper.createObjectNode();
            negativePrompt.put("text", request.getNegativePrompt());
            negativePrompt.put("weight", -1);
            textPrompts.add(negativePrompt);
        }

        requestBody.set("text_prompts", textPrompts);
        requestBody.put("cfg_scale", request.getCfgScale());
        requestBody.put("height", request.getHeight());
        requestBody.put("width", request.getWidth());
        requestBody.put("samples", request.getSamples());
        requestBody.put("steps", request.getSteps());

        // 添加样式预设（如果支持）
        if (request.getStyle() != null && !request.getStyle().isEmpty()) {
            requestBody.put("style_preset", request.getStyle());
        }

        return requestBody;
    }

    private List<ImageGenerationResponse.GeneratedImage> parseResponse(JsonNode responseJson) {
        List<ImageGenerationResponse.GeneratedImage> images = new ArrayList<>();

        JsonNode artifacts = responseJson.path("artifacts");
        if (artifacts.isArray()) {
            for (JsonNode artifact : artifacts) {
                String base64Image = artifact.path("base64").asText();

                // 保存图像并获取ID
                String imageId = imageStorageService.saveBase64Image(base64Image);

                // 创建生成的图像对象
                ImageGenerationResponse.GeneratedImage image = ImageGenerationResponse.GeneratedImage.builder()
                        .imageId(imageId)
                        .base64Image(base64Image)
                        .imageUrl("/api/images/" + imageId)
                        .width(artifact.path("width").asInt())
                        .height(artifact.path("height").asInt())
                        .seed(artifact.path("seed").asLong())
                        .build();

                images.add(image);
            }
        }

        return images;
    }


}
