package com.wtu.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/04/13/21:00
 * @Description: 文生图功能请求参数封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextToImageDTO {
    @NotBlank(message = "提示文本不能为空")
    @Size(min = 3, max = 1000, message = "提示文本长度必须在3到1000个字符之间")
    private String prompt;

    /**
     * 负面提示词，用于排除不想出现的元素
     * 默认为null
     */
    private String negativePrompt;

    /**
     * 生成图像的数量
     * 默认值: 1
     */
    @Min(value = 1, message = "至少生成1张图像")
    @Max(value = 4, message = "最多生成4张图像")
    private int samples;

    /**
     * 图像宽度像素
     * 默认值: 1024
     */
    @Min(value = 512, message = "图像宽度最小为512")
    @Max(value = 1024, message = "图像宽度最大为1024")
    private int width;

    /**
     * 图像高度像素
     * 默认值: 1024
     */
    @Min(value = 512, message = "图像高度最小为512")
    @Max(value = 1024, message = "图像高度最大为1024")
    private int height;

    /**
     * 采样步数，影响生成质量和时间
     * 默认值: 30
     */
    @Min(value = 1, message = "步数最小为1")
    @Max(value = 150, message = "步数最大为150")
    private int steps;

    /**
     * CFG尺度，控制提示词的相关性强度
     * 默认值: 7.0
     */
    @Min(value = 1, message = "CFG尺度最小为1")
    @Max(value = 35, message = "CFG尺度最大为35")
    private double cfgScale;

    /**
     * 样式预设
     * 可选值如: "3d-model", "analog-film", "anime", "cinematic", "comic-book",
     * "digital-art", "enhance", "fantasy-art", "isometric", "line-art",
     * "low-poly", "modeling-compound", "neon-punk", "origami", "photographic",
     * "pixel-art", "tile-texture"
     * 不支持其他值，否则API会返回bad_request错误
     * 默认值: "" (空字符串)
     */
    private String style;
}
