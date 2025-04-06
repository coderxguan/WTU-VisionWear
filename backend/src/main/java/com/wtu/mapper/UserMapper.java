package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wtu.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 这里可以添加自定义的方法
    // 例如：List<User> findByUsername(String username);

}
