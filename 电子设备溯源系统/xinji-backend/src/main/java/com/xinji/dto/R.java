package com.xinji.dto;

import lombok.Data;

@Data
public class R<T> {
    private Integer code;
    private String message;
    private T data;

    private R() {}

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.code = 200;
        r.message = "success";
        r.data = data;
        return r;
    }

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> error(Integer code, String message) {
        R<T> r = new R<>();
        r.code = code;
        r.message = message;
        r.data = null;
        return r;
    }

    public static <T> R<T> error(String message) {
        return error(500, message);
    }
}
