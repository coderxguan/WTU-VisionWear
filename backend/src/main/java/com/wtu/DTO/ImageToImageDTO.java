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
     * 默认值: 7.0
     */
    private float cfgScale;

    /**
     * 图像强度 (保留原图的程度, 值越低越接近原图)
     * 范围必须在0到1之间
     * 默认值: 0.35
     */
    private float imageStrength;

    /**
     * 生成图像的数量
     * 通常范围为1-10
     * 默认值: 1
     */
    private int samples;

    /**
     * 采样步数
     * 通常范围为10-150，越高质量越好但耗时更长
     * 默认值: 30
     */
    private int steps;

    /**
     * 样式预设
     * 可选值如: "3d-model", "analog-film", "anime", "cinematic", "comic-book",
     * "digital-art", "enhance", "fantasy-art", "isometric", "line-art",
     * "low-poly", "modeling-compound", "neon-punk", "origami", "photographic",
     * "pixel-art", "tile-texture"
     * 默认值: null (不应用样式预设)
     */
    private String style;

    /**
     * 随机种子
     * 为null或负数时使用随机种子，指定正整数时可重现结果
     * 默认值: null (使用随机种子)
     */
    private Long seed;
}