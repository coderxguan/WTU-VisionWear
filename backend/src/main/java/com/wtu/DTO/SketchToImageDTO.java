package com.wtu.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SketchToImageDTO {
    /**
     * 线稿图片ID
     */
    private String sketchImageId;

    /**
     * 生成提示词
     */
    private String prompt;

    /**
     * 返回图片类型 (url/base64)
     */
    @Builder.Default
    private String rspImgType = "url";

    /**
     * 生成配置（可根据API文档扩展）
     */
    @Builder.Default
    private String config = "default";
}