package com.wtu.utils;

import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/04/13/21:03
 * @Description: 处理stable传递的图片参数工具类
 */
public class ImageUtils {
    private static final Pattern BASE64_PATTERN = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");

    private ImageUtils() {
        // 工具类不应该被实例化
    }

    /**
     * 验证字符串是否为有效的Base64编码
     * @param base64String 要验证的Base64字符串
     * @return 如果是有效的Base64则返回true
     */
    public static boolean isValidBase64(String base64String) {
        return base64String != null && BASE64_PATTERN.matcher(base64String).matches();
    }

    /**
     * 从Base64字符串中提取纯Base64内容（移除可能的前缀如"data:image/png;base64,"）
     * @param base64String 可能带有前缀的Base64字符串
     * @return 纯Base64内容
     */
    public static String extractBase64Content(String base64String) {
        if (base64String == null) {
            return null;
        }

        int commaIndex = base64String.indexOf(',');
        if (commaIndex > 0) {
            return base64String.substring(commaIndex + 1);
        }

        return base64String;
    }

    /**
     * 验证Base64编码的图像大小是否在限制范围内
     * @param base64Image Base64编码的图像
     * @param maxSizeBytes 最大允许大小（字节）
     * @return 如果图像大小在限制范围内则返回true
     */
    public static boolean validateImageSize(String base64Image, long maxSizeBytes) {
        if (base64Image == null) {
            return false;
        }

        String cleanBase64 = extractBase64Content(base64Image);
        byte[] imageBytes = Base64.getDecoder().decode(cleanBase64);
        return imageBytes.length <= maxSizeBytes;
    }

}
