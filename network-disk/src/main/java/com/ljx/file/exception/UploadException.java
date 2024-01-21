package com.ljx.file.exception;

/**
 * 文件上传异常
 * @Author: ljx
 * @Date: 2023/12/21 15:50
 */
public class UploadException extends Exception{
    public UploadException() {

    }
    public UploadException(Throwable cause) {
        super("上传出现了异常", cause);
    }

    public UploadException(String message) {
        super(message);
    }

    public UploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
