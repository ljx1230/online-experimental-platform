package com.ljx.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljx.file.common.RestResult;
import com.ljx.file.model.User;

/**
 * @Author: ljx
 * @Date: 2023/12/20 15:19
 */
public interface UserService extends IService<User> {
    RestResult<String> registerUser(User user);
    RestResult<User> login(User user);
    User getUserByToken(String token);
}
