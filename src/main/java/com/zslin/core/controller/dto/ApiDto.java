package com.zslin.core.controller.dto;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class ApiDto {

    private Method method;

    private Object obj;

    private boolean hasParams;

    private String params;

    public ApiDto(Method method, Object obj, boolean hasParams, String params) {
        this.method = method;
        this.obj = obj;
        this.hasParams = hasParams;
        this.params = params;
    }
}
