package com.ljx.file.config.jwt;

import lombok.Data;

/**
 * @Author: ljx
 * @Date: 2023/12/20 13:24
 */
@Data
public class JwtPayload {
    private RegisterClaims registerClaims;
}
