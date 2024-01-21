package com.ljx.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: ljx
 * @Date: 2023/12/22 16:51
 */
@Data
@Schema(name = "批量移动文件DTO",required = true)
public class BatchMoveFileDTO {
    @Schema(description="文件集合")
    private String files;
    @Schema(description="文件路径")
    private String filePath;
}
