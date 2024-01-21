package com.ljx.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljx.file.common.RestResult;
import com.ljx.file.mapper.StorageMapper;
import com.ljx.file.mapper.UserMapper;
import com.ljx.file.model.Storage;
import com.ljx.file.model.User;
import com.ljx.file.service.UserService;
import com.ljx.file.util.DateUtil;
import com.ljx.file.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.security.auth.Subject;
import java.util.List;
import java.util.UUID;

/**
 * @Author: ljx
 * @Date: 2023/12/20 15:19
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private StorageMapper storageMapper;
    @Override
    public RestResult<String> registerUser(User user) {
        String telephone = user.getTelephone();
        String password = user.getPassword();
        if(!StringUtils.hasLength(telephone) || !StringUtils.hasLength(password)) {
            return RestResult.fail().message("手机号以及密码不能为空!");
        }
        if(telephoneExist(telephone)) {
            return RestResult.fail().message("手机号已存在!");
        }
        String salt = UUID.randomUUID().toString().replace("-","").substring(15);
        String passwordAndSalt = password + salt;
        String newPassword = DigestUtils.md5DigestAsHex(passwordAndSalt.getBytes()); // 生成md5码
        user.setPassword(newPassword);
        user.setSalt(salt);
        user.setRegisterTime(DateUtil.getCurrentTime());
        int success = userMapper.insert(user);
        Storage storage = new Storage();
        storage.setUserId(user.getUserId());
        storage.setStorageSize((long) 1024*1024*1024);
        storageMapper.insert(storage);
        if(success != 1) {
            return RestResult.fail().message("注册失败，请检查输入信息!");
        }
        return RestResult.success();
    }

    @Override
    public RestResult<User> login(User user) {
        String telephone = user.getTelephone();
        String password = user.getPassword();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getTelephone,telephone);
        User saveUser = userMapper.selectOne(wrapper);
        String salt = saveUser.getSalt();
        String passwordAndSalt = password + salt;
        String newPassword = DigestUtils.md5DigestAsHex(passwordAndSalt.getBytes());
        if(newPassword.equals(saveUser.getPassword())) {
            saveUser.setPassword("");
            saveUser.setSalt("");
            return  RestResult.success().data(saveUser);
        }
        return RestResult.fail().message("手机号或密码错误！");
    }

    @Override
    public User getUserByToken(String token) {
        User tokenUserInfo = null;
        try {
            Claims claims = jwtUtil.parseJWT(token);
            String subject = claims.getSubject();
            ObjectMapper objectMapper = new ObjectMapper();
            tokenUserInfo = objectMapper.readValue(subject,User.class);
        } catch (Exception e) {
            log.error("token解析异常");
            e.printStackTrace();
            return null;
        }
        return tokenUserInfo;
    }

    private boolean telephoneExist(String telephone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getTelephone,telephone);
        List<User> userList = userMapper.selectList(wrapper);
        if(userList != null && !userList.isEmpty()) {
            return true;
        }
        return false;
    }

}
