package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wtu.entity.Material;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/05/24/17:26
 * @Description: 素材库Mapper类
 */
@Mapper
public interface MaterialMapper extends BaseMapper<Material> {
}
