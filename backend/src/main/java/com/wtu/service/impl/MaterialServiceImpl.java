package com.wtu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wtu.entity.Material;
import com.wtu.mapper.MaterialMapper;
import com.wtu.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/05/24/17:28
 * @Description: 素材接口类
 */
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

   private final MaterialMapper materialMapper;

    @Override
    public List<String> getMaterial() {
        LambdaQueryWrapper<Material> queryWrapper= new LambdaQueryWrapper<>();
        queryWrapper.eq(Material::getStatus,"0");
        List<Material> materials = materialMapper.selectList(queryWrapper);
        ArrayList<String> result=new ArrayList<>();
        for (Material material : materials) {
            result.add(material.getImageUrl());
        }
        return result;
    }
}
