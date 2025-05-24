package com.wtu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/05/24/17:16
 * @Description: 素材表DO类
 */
@Data
@Schema(description = "素材表")
public class Material {
    @TableId("material_id")
    @Schema(description = "素材唯一id")
    private String materialId;

    @TableField("image_url")
    @Schema(description = "图片OSS地址")
    private String imageUrl;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField("status")
    @Schema(description = "图片状态（0-正常，1-已删除）")
    private Integer status;

}
