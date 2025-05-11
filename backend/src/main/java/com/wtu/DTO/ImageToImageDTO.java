package com.wtu.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageToImageDTO {
    /**
     * 源图像url
     */
    private String sourceImageUrl;

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 负面提示词
     */
    private String negativePrompt;

    /**
     * CFG Scale (提示词引导强度)
     * 范围通常为1-35，越高越遵循提示词
     */
    @Builder.Default
    private float cfgScale = 7.0f;

    /**
     * 图像强度 (保留原图的程度, 值越低越接近原图)
     * 范围必须在0到1之间
     */
    @Builder.Default
    private float imageStrength = 0.35f;

    /**
     * 生成图像的数量
     * 通常范围为1-10
     */
    @Builder.Default
    private int samples = 1;

    /**
     * 采样步数
     * 通常范围为10-150，越高质量越好但耗时更长
     */
    @Builder.Default
    private int steps = 30;

    /**
     * 样式预设
     * 可选值如: "3d-model", "analog-film", "anime", "cinematic", "comic-book",
     * "digital-art", "enhance", "fantasy-art", "isometric", "line-art",
     * "low-poly", "modeling-compound", "neon-punk", "origami", "photographic",
     * "pixel-art", "tile-texture"
     */
    private String style;

    /**
     * 随机种子
     * 为null或负数时使用随机种子，指定正整数时可重现结果
     */
    private Long seed;
}