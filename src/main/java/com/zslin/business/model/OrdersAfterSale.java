package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 订单售后
 * @author 钟述林
 * @data generate on: 2020-01-05
 */
@Data
@Entity
@Table(name = "business_orders_after_sale")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrdersAfterSale implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 订单ID
	*/
	private Integer ordersId;

	/**
	* 订单编号
	*/
	private String ordersNo;

	private String openid;

	private String unionid;

	/**
	* 处理内容
	*/
	@Lob
	private String content;

	/**
	* 是否退款
	* @remark 0-未退款；1-退款
	*/
	private String hasRefund="0";

	/**
	* 退款金额
	*/
	private Integer refundMoney=0;

}
