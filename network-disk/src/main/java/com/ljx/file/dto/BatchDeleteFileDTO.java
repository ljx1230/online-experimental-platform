package com.ljx.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: ljx
 * @Date: 2023/12/22 15:23
 */
@Schema(name = "批量删除文件DTO",required = true)
@Data
public class BatchDeleteFileDTO {
    @Schema(description = "文件集合")
    private String files;
}
