package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 订单产品
 * @author 钟述林
 * @data generate on: 2019-12-18
 */
@Data
@Entity
@Table(name = "business_orders_product")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrdersProduct implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 消费者Openid
	*/
	private String openid;

	/**
	* 消费者Unionid
	*/
	private String unionid;

	/**
	* 订单编号
	*/
	private String OrdersNo;

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

	/**
	* 代理等级ID
	* @remark 对应代理当前等级
	*/
	private Integer agentLevelId;

	/**
	* 代理等级名称
	* @remark 对应代理当前等级
	*/
	private String agentLevelName;

	/**
	* 产品ID
	*/
	private Integer proId;

	/**
	* 产品标题
	*/
	private String proTitle;

	/**
	* 产品规格ID
	*/
	private Integer specsId;

	/**
	* 产品规格名称
	*/
	private String specsName;

	/**
	* 产品原价
	*/
	private Float oriPrice;

	/**
	* 产品单价
	*/
	private Float price;

	/**
	* 产品数量
	*/
	private Integer amount;

	/**
	* 基金金额
	* @remark 单个产品基金金额*amount
	*/
	private Float fund;

}