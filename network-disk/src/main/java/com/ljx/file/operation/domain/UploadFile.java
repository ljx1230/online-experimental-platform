package com.ljx.file.operation.domain;

import lombok.Data;

/**
 * @Author: ljx
 * @Date: 2023/12/21 18:45
 */
@Data
public class UploadFile {
    private String fileName;
    private String fileType;
    private long fileSize;
    private String timeStampName;
    private int success;
    private String message;
    private String url;
    //切片上传相关参数
    private String taskId;
    private int chunkNumber;
    private long chunkSize;
    private int totalChunks;
    private String identifier;
    private long totalSize;
    private long currentChunkSize;
}
