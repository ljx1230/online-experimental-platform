package com.ljx.file.config.jwt;

import lombok.Data;

/**
 * @Author: ljx
 * @Date: 2023/12/20 13:25
 */
@Data
public class RegisterClaims {
    private String iss; // 签发者
    private String exp; // 失效时间
    private String sub; // 用户信息
    private String aud; // 接收者
}
