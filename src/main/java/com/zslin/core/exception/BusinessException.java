package com.zslin.core.exception;

import lombok.Data;

/**
 * 业务异常
 */
@Data
public class BusinessException extends RuntimeException {

    private String code;

    private String msg;

    public BusinessException() {
        super();
    }

    public BusinessException(String msg) {super(msg); this.msg = msg;}

    public BusinessException(String code, String msg) {super(msg); this.code = code; this.msg = msg;}
}
