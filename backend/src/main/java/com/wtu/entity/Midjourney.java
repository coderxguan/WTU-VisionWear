package com.wtu.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "midjourney调用数据类型封装实体")
public class Midjourney {
    @Schema(description = "调用请求所需密钥")
    @NotBlank
    private String Mjtoken;

    @Schema(description = "指定服务端返回的格式")
    //如果是 application/json 则一次性返回结果，如果是 application/x-ndjson 则是流式返回结果。默认是 application/json"
    private String accept="application/json";

    @Schema(description = "操作类型")
    //在第一次生成预览图时，需要指定该值为 generate，并填入 prompt 字段。
    private String action;

    @Schema(description = "图像描述")
    private String prompt;

    @Schema(description = "图像id")
    //在第一次生成预览图时，不需要指定该字段。在后续需要对图像进行处理时，需要指定该字段，代表待处理图像的 ID。该 ID 即为第一次生成预览图时返回的 image_id 字段
    private String imageId;

}
