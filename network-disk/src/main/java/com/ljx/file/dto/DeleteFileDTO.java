package com.ljx.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: ljx
 * @Date: 2023/12/22 15:18
 */
@Data
@Schema(name = "删除文件DTO",required = true)
public class DeleteFileDTO {
    @Schema(description = "用户文件id")
    private Long userFileId;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "是否是目录")
    private Integer isDir;
}
