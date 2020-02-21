package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 代理佣金明细
 * @author 钟述林
 * @data generate on: 2020-02-20
 */
@Data
@Entity
@Table(name = "business_custom_commission_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomCommissionRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 代理ID
	*/
	private Integer agentId;

	private String agentOpenid;

	private String agentUnionid;

	private String agentName;

	private String agentPhone;

	/**
	* 订单编号
	*/
	private String ordersNo;

	/**
	* 订单ID
	*/
	private Integer ordersId;

	/**
	* 规格ID
	*/
	private Integer specsId;

	/**
	* 规格名称
	*/
	private String specsName;

	/**
	* 佣金金额
	*/
	private Float money;

	/**
	* 产品ID
	*/
	private Integer proId;

	/**
	* 产品标题
	*/
	private String proTitle;

	/**
	* 状态
	* @remark 0-用户下单；1-用户付款，但不在提现期；2-在提现期；3-纳入结算清单；4-结算到账；5-结算失败
	*/
	private String status;

	private String createTime;

	private String createDay;

	private Long createLong;

	/**
	* 对应用户ID
	*/
	private Integer customId;

	/**
	* 对应用户昵称
	*/
	private String customNickname;

	/**
	* 对应用户Openid
	*/
	private String customOpenid;

	/**
	* 对应用户Unionid
	*/
	private String customUnionid;

}
