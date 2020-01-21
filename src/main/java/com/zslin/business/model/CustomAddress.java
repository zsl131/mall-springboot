package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;


/**
 * 客户收货地址
 * @author 钟述林
 * @data generate on: 2020-01-10
 */
@Data
@Entity
@Table(name = "business_custom_address")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomAddress implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String openid;

	private String unionid;

	/**
	* 省级代码
	*/
	@NotBlank(message="省份不能为空")
	private String provinceCode;

	/**
	* 省级名称
	*/
	private String provinceName;

	/**
	* 市级代码
	*/
	@NotBlank(message="市不能为空")
	private String cityCode;

	/**
	* 市级名称
	*/
	private String cityName;

	/**
	* 县级代码
	*/
	@NotBlank(message="县区不能为空")
	private String countyCode;

	/**
	* 县级名称
	*/
	private String countyName;

	/**
	* 街道地址
	*/
	@NotBlank(message="街道不能为空")
@Length(min=5, message="街道至少5个字")
	private String street;

	/**
	* 姓名
	*/
	@NotBlank(message="联系人不能为空")
	private String name;

	/**
	* 联系电话
	*/
	@NotBlank(message="联系电话不能为空")
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
