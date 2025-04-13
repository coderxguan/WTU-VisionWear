package com.wtu.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/04/13/20:52
 * @Description: 以文生图响应参数封装类
 */

@Data
@Builder
public class ImageGenerationResponse {

    private String requestId;
    private List<GeneratedImage> images;
    private String prompt;
    private long generationTimeMs;

    @Data
    @Builder
    public static class GeneratedImage {
        private String imageId;
        private String base64Image;
        private String imageUrl;
        private int width;
        private int height;
        private long seed;
    }


}
