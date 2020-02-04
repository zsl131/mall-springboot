package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 订单
 * @author 钟述林
 * @data generate on: 2020-02-04
 */
@Data
@Entity
@Table(name = "business_orders")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Orders implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 订单编号
	*/
	private String ordersNo;

	private String openid;

	private String unionid;

	/**
	* 订单内总产品数量
	*/
	private Integer totalCount;

	/**
	* 订单总金额
	*/
	private Float totalMoney;

	/**
	* 订单总优惠金额
	*/
	private Float discountMoney;

	/**
	* 订单实付金额
	* @remark 不含运费
	*/
	private Float payMoney;

	/**
	* 订单总基金金额
	*/
	private Float fundMoney;

	/**
	* 运费金额
	*/
	private Float freight;

	/**
	* 订单日期
	* @remark 格式：yyyy-MM-dd
	*/
	private String createDay;

	/**
	* 订单时间
	* @remark 格式：yyyy-MM-dd HH:mm:ss
	*/
	private String createTime;

	/**
	* 订单时间
	* @remark Long格式
	*/
	private Long createLong;

	/**
	* 付款日期
	*/
	private String payDay;

	/**
	* 付款时间
	*/
	private String payTime;

	/**
	* 付款时间Long类型
	*/
	private Long payLong;

	/**
	* 订单状态
	* @remark 0-未付款；1-已付款，未发货；2-已发货；3-已完成
	*/
	private String status;

	/**
	* 是否存在售后
	* @remark 0-不存在；1-存在；默认0
	*/
	private String hasAfterSale="0";

	/**
	* 代理Openid
	*/
	private String agentOpenid;

	/**
	* 代理unionid
	*/
	private String agentUnionid;

}
