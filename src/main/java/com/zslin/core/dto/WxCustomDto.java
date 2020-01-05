package com.zslin.core.dto;

import lombok.Data;

/**
 * 小程序传入参数
 * - 用户DTO对象
 */
@Data
public class WxCustomDto {

    private Integer customId;

    private String openid;

    private String unionid;

    private String nickname;
}
