package com.ljx.file.exception;


/**
 * MD5校验异常
 * @Author: ljx
 * @Date: 2023/12/21 15:49
 */
public class NotSameFileException extends Exception {
    public NotSameFileException() {
        super("File MD5 Different");
    }
}
