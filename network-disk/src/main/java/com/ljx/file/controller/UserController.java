package com.ljx.file.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljx.file.common.RestResult;
import com.ljx.file.dto.RegisterDTO;
import com.ljx.file.model.User;
import com.ljx.file.service.UserService;
import com.ljx.file.util.JwtUtil;
import com.ljx.file.vo.LoginVo;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: ljx
 * @Date: 2023/12/19 19:38
 */
@Tag(name = "user", description = "该接口为用户接口，主要做用户登录，注册和校验token")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     * @param registerDTO
     * @return
     */
    @Operation(summary = "用户注册", description = "注册账号", tags = {"user"})
    @PostMapping(value = "/register")
    public RestResult<String> register(@RequestBody RegisterDTO registerDTO) {

        User user = new User();
        BeanUtils.copyProperties(registerDTO,user);

        return userService.registerUser(user);
    }

    /**
     * 用户登录
     * @param telephone
     * @param password
     * @return
     */
    @Operation(summary = "用户登录", description = "用户登录认证后才能进入系统", tags = {"user"})
    @GetMapping("/login")
    public RestResult<LoginVo> userLogin(String telephone,String password) {
        RestResult<LoginVo> restResult = new RestResult<>();
        LoginVo loginVo = new LoginVo();
        User user = new User();
        user.setTelephone(telephone);
        user.setPassword(password);
        RestResult<User> loginRes = userService.login(user);
        if(!loginRes.isSuccess()) {
            return RestResult.fail().message("登录失败");
        }
        user = loginRes.getData();
        loginVo.setUsername(user.getUsername());
        String jwt = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            jwt = jwtUtil.createJwt(objectMapper.writeValueAsString(user));
        } catch (Exception e) {
            e.printStackTrace();
            return RestResult.fail().message("登录失败,请检查登录信息!");
        }
        loginVo.setToken(jwt);
        return RestResult.success().data(loginVo);
    }

    /**
     * 检测用户是否登录
     * @param token
     * @return
     */
    @Operation(summary = "检查用户登录信息", description = "验证token的有效性", tags = {"user"})
    @GetMapping("/checkuserlogininfo")
    public RestResult<User> checkToken(@RequestHeader("token") String token) {
        RestResult<User> restResult = new RestResult<>();
        User tokenUserInfo = null;
        try {
            Claims claims = jwtUtil.parseJWT(token);
            String subject = claims.getSubject();
            ObjectMapper objectMapper = new ObjectMapper();
            tokenUserInfo = objectMapper.readValue(subject,User.class);
        } catch (Exception e) {
            // log.error("jwt解析异常");
            return RestResult.fail().message("用户认证失败!");
        }
        if(tokenUserInfo == null) {
            return RestResult.fail().message("用户未登录!");
        }
        return RestResult.success().data(tokenUserInfo);
    }

}
