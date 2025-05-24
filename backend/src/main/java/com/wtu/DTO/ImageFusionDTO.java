package com.wtu.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 请求参数DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageFusionDTO {
    @NotNull(message = "图片Base64列表不能为空")
    @Size(min = 2, max = 5, message = "图片数量必须在2到5之间")
    private List<String> imageUrlList;

    private String dimensions = "SQUARE"; // 默认方形

    private String mode = "relax"; // 图像的任务模式，包括 fast 、 relax 和 turbo 。如果未填写，将默认使用快速模式。

    private String hookUrl; // 可选回调地址
}