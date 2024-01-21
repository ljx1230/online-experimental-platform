package com.ljx.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: ljx
 * @Date: 2023/12/21 20:59
 */
@Data
@Schema(name = "下载文件DTO",required = true)
public class DownloadFileDTO {
    private Long userFileId;
}
