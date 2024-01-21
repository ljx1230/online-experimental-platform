package com.ljx.file.common;

import lombok.Data;

/**
 * @Author: ljx
 * @Date: 2023/12/19 19:35
 */
@Data
public class RestResult<T> {
    private boolean success = true;
    private Integer code;
    private String message;
    private T data;

    public static RestResult success() {
        RestResult r = new RestResult();
        r.setSuccess(ResultCodeEnum.SUCCESS.getSuccess());
        r.setCode(ResultCodeEnum.SUCCESS.getCode());
        r.setMessage(ResultCodeEnum.SUCCESS.getMessage());
        return r;
    }
    public static RestResult fail() {
        RestResult r = new RestResult();
        r.setSuccess(ResultCodeEnum.UNKNOWN_ERROR.getSuccess());
        r.setCode(ResultCodeEnum.UNKNOWN_ERROR.getCode());
        r.setMessage(ResultCodeEnum.UNKNOWN_ERROR.getMessage());
        return r;
    }

    // 自定义返回数据
    public RestResult data(T param) {
        this.setData(param);
        return this;
    }

    // 自定义状态信息
    public RestResult message(String message) {
        this.setMessage(message);
        return this;
    }

    // 自定义状态码
    public RestResult code(Integer code) {
        this.setCode(code);
        return this;
    }

    // 设置结果，形参为结果枚举
    public static RestResult setResult(ResultCodeEnum result) {
        RestResult r = new RestResult();
        r.setSuccess(result.getSuccess());
        r.setCode(result.getCode());
        r.setMessage(result.getMessage());
        return r;
    }

}
