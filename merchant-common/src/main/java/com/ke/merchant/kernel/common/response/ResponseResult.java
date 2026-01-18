package com.merchant.common.response;

public class ResponseResult<T> {
    private boolean success;
    private String code;
    private String msg;
    private T data;

    public static <T> ResponseResult<T> success(T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.success = true;
        result.code = "SUCCESS";
        result.data = data;
        return result;
    }

    public static <T> ResponseResult<T> success() {
        return success(null);
    }

    public static <T> ResponseResult<T> fail(String code, String msg) {
        ResponseResult<T> result = new ResponseResult<>();
        result.success = false;
        result.code = code;
        result.msg = msg;
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
