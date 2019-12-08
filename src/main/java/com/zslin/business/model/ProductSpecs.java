package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 产品规格
 * @author 钟述林
 * @data generate on: 2019-12-08
 */
@Data
@Entity
@Table(name = "business_product_specs")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductSpecs implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
	
	/**
	* 规格名称
	* @remark 如：大果12斤装
	*/
	private String name;

	/**
	* 描述
	* @remark 有则显示
	*/
	private String remark;

	/**
	* 排序序号
	* @remark 越小越靠前
	*/
	private Integer orderNo;

	/**
	* 原价
	*/
	private Float oriPrice;

	/**
	* 现价
	*/
	private Float price;

	/**
	* 产品ID
	* @remark 外键
	*/
	private Integer proId;

	/**
	* 产品标题
	* @remark 外键
	*/
	private String proTitle;

}
