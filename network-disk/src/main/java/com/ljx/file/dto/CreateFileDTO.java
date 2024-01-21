package com.ljx.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: ljx
 * @Date: 2023/12/21 13:32
 */
@Data
@Schema(name = "创建文件DTO",required = true)
public class CreateFileDTO {
    @Schema(description = "文件名")
    private String fileName;
    @Schema(description = "文件路径")
    private String filePath;
}
