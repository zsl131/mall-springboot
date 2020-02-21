package com.zslin.business.app.dto;

import lombok.Data;

/**
 * 订单处理DTO对象
 */
@Data
public class OrdersHandlerDto {

    /** 基金金额 */
    private Float fundMoney=0f;
    /** 总件数 */
    private Integer specsCount=0;
    /** 总佣金金额 */
    private Float TotalCommission=0f;
    /** 产品总数量 */
    private Integer totalCount=0;
    /** 总金额 */
    private Float totalMoney=0f;
}
