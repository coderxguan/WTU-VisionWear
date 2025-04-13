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
public class ImageGenerationRequest {
    @NotBlank(message = "提示文本不能为空")
    @Size(min = 3, max = 1000, message = "提示文本长度必须在3到1000个字符之间")
    private String prompt;

    private String negativePrompt;

    @Min(value = 1, message = "至少生成1张图像")
    @Max(value = 4, message = "最多生成4张图像")
    private int samples = 1;

    @Min(value = 512, message = "图像宽度最小为512")
    @Max(value = 1024, message = "图像宽度最大为1024")
    private int width = 1024;

    @Min(value = 512, message = "图像高度最小为512")
    @Max(value = 1024, message = "图像高度最大为1024")
    private int height = 1024;

    @Min(value = 1, message = "步数最小为1")
    @Max(value = 150, message = "步数最大为150")
    private int steps = 30;

    @Min(value = 1, message = "CFG尺度最小为1")
    @Max(value = 35, message = "CFG尺度最大为35")
    private double cfgScale = 7.0;

    private String style = "     ";
}
