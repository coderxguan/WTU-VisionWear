package com.wtu.VO;

import lombok.*;

import java.util.List;

/**
 * 线稿生图
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SketchToImageVO {
    private String requestId;
    private List<GeneratedImage> images;
    private String sketchImageId;
    private Long generationTimeMs;

    @Data
    @Builder
    public static class GeneratedImage {
        private String imageId;
        private String imageUrl;
    }
}