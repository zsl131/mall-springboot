package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 退款记录
 * @author 钟述林
 * @data generate on: 2020-07-29
 */
@Data
@Entity
@Table(name = "business_refund_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefundRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String ordersNo;

	private Integer ordersId;

	/**
	* 退款单号
	*/
	private String refundNo;

	private Integer ordersProId;

	private String ordersProTitle;

	private Float backMoney=0f;

	private String createDay;

	private String createTime;

	private Long createLong;

	private String agentOpenid;

	private String agentName;

	private String agentPhone;

	/**
	* 操作员名称
	*/
	private String optName;

	/**
	* 操作员用户名
	*/
	private String optUsername;

	/**
	* 操作员ID
	*/
	private Integer optUserId;

	/**
	* 退款原因
	*/
	private String reason;

	/**
	* 退款状态
	* @remark 0-成功；-1-失败
	*/
	private String status="0";

	/**
	* 退款结果代码
	*/
	private String resCode;

	/**
	* 退款结果信息
	*/
	private String resCodeDes;

}
