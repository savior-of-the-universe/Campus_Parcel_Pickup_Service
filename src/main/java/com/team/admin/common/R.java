package com.team.admin.common;

import lombok.Data;

@Data
public class R<T> {
    private Integer code;
    private String msg;
    private T data;

    private R(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> R<T> ok(T data) {
        return new R<>(200, "success", data);
    }

    public static <T> R<T> ok(String msg, T data) {
        return new R<>(200, msg, data);
    }

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> error(String msg) {
        return new R<>(500, msg, null);
    }

    public static <T> R<T> error(Integer code, String msg) {
        return new R<>(code, msg, null);
    }
}
