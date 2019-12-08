package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 客户收货地址
 * @author 钟述林
 * @data generate on: 2019-12-08
 */
@Data
@Entity
@Table(name = "business_custom_address")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomAddress implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
	
	/**
	* 
	*/
	private String openid;

	/**
	* 
	*/
	private String unionid;

	/**
	* 省级代码
	*/
	private String provinceCode;

	/**
	* 省级名称
	*/
	private String provinceName;

	/**
	* 市级代码
	*/
	private String cityCode;

	/**
	* 市级名称
	*/
	private String cityName;

	/**
	* 县级代码
	*/
	private String countyCode;

	/**
	* 县级名称
	*/
	private String countyName;

	/**
	* 街道地址
	*/
	private String street;

	/**
	* 联系电话
	*/
	private String phone;

	/**
	* 邮编
	*/
	private String postCode;

	/**
	* 是否默认
	* @remark 0-不是默认；1-默认
	*/
	private String isDefault;

}
