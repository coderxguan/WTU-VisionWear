package com.wtu.service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/05/24/17:28
 * @Description:
 */
public interface MaterialService {

    /**
     * 获取所有素材库URL
     *
     * @return 图像URL列表
     */
    List<String> getMaterial();
}
