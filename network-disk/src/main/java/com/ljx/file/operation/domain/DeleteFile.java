package com.ljx.file.operation.domain;

import lombok.Data;

/**
 * @Author: ljx
 * @Date: 2023/12/21 20:54
 */
@Data
public class DeleteFile {
    private String fileUrl;
    private String timeStampName;
}
