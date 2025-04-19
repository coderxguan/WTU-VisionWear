package com.wtu.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/04/19/18:39
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageToImageDTO {
    @Schema(description = "源图像ID")
    private String sourceImageId;

    /**
     * 提示词
     */
    @Schema(description = "提示词")
    private String prompt;

    /**
     * 负面提示词
     */
    @Schema(description = "负面提示词")
    private String negativePrompt;

    /**
     * CFG Scale (提示词引导强度)
     */
    @Builder.Default
    @Schema(description = "CFG Scale (提示词引导强度)")
    private float cfgScale = 7.0f;

    /**
     * 图像强度 (保留原图的程度, 值越低越接近原图)
     */
    @Builder.Default
    @Schema(description = "图像强度 (保留原图的程度, 值越低越接近原图)")
    private float imageStrength = 0.35f;

    /**
     * 生成图像的数量
     */
    @Builder.Default
    @Schema(description = "生成图像的数量")
    private int samples = 1;

    /**
     * 采样步数
     */
    @Builder.Default
    @Schema(description = "采样步数")
    private int steps = 30;

    /**
     * 样式预设
     */
    @Schema(description = "样式预设")
    private String style;
}
