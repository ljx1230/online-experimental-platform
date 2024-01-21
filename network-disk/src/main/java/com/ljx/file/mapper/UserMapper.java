package com.ljx.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljx.file.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: ljx
 * @Date: 2023/12/19 16:44
 */
public interface UserMapper extends BaseMapper<User> {
    void insertUser(User user);
    List<User> selectUser();
}
