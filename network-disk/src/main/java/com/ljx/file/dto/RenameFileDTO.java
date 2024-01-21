package com.ljx.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: ljx
 * @Date: 2023/12/24 20:48
 */
@Data
@Schema(name = "重命名文件",required = true)
public class RenameFileDTO {
    private Long userFileId;
    @Schema(description = "文件名")
    private String fileName;
}
