package com.ljx.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: ljx
 * @Date: 2023/12/20 15:51
 */
@Schema(description="登录VO")
@Data
public class LoginVo {
    @Schema(description="用户名")
    private String username;
    @Schema(description="token")
    private String token;
}
