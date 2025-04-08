package com.wtu.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文生图中后端接收数据类型")
@Builder
public class MidjourneyTextDTO {

    @Schema(description = "操作类型")
    //在第一次生成预览图时，需要指定该值为 generate，并填入 prompt 字段。
    private String action;

    @Schema(description = "图像描述")
    private String prompt;

    @Schema(description = "图像id")
    //在第一次生成预览图时，不需要指定该字段。在后续需要对图像进行处理时，需要指定该字段，代表待处理图像的 ID。该 ID 即为第一次生成预览图时返回的 image_id 字段
    private String imageId;
}
