package com.ljx.file.config.jwt;

import lombok.Data;

/**
 * @Author: ljx
 * @Date: 2023/12/20 13:21
 */
@Data
public class JwtHeader {
    private String alg; // 签名算法
    private String typ; // 类型："jwt"
}
