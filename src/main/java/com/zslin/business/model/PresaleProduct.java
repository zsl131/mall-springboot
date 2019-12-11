package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 预售产品
 * @author 钟述林
 * @data generate on: 2019-12-09
 */
@Data
@Entity
@Table(name = "business_presale_product")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresaleProduct implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
	
	/**
	* 产品ID
	* @remark 外键
	*/
	private Integer proId;

	/**
	* 产品名称
	* @remark 外键
	*/
	private String proTitle;

	/**
	* 显示状态
	* @remark 0-隐藏；1-显示
	*/
	private String status;

	/**
	* 预计发货日期
	*/
	private String deliveryDate;

}
