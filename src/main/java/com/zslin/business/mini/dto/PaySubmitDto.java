package com.zslin.business.mini.dto;

import lombok.Data;

/** 提交支付的DTO对象 */
@Data
public class PaySubmitDto {

    private String appId;

    private String timeStamp;

    private String nonceStr;

    private String packageStr;

    private String paySign;

    private String signType = "MD5";
}
