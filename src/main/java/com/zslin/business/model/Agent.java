package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;


/**
 * 代理
 * @author 钟述林
 * @data generate on: 2019-12-24
 */
@Data
@Entity
@Table(name = "business_agent")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Agent implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 姓名
	*/
	@NotBlank(message="姓名不能为空")
	private String name;

	/**
	* 状态
	* @remark 0-申请；1-正式代理；2-驳回申请；
	*/
	private String status;

	/**
	* 联系电话
	*/
	@NotBlank(message="联系电话不能为空")
	private String phone;

	/**
	* 身份证号
	*/
	@NotBlank(message="身份证号不能为空")
	private String identity;

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
	* 县级代码
	*/
	private String countyName;

	/**
	* 性别
	* @remark 1-男；2-女
	*/
	@Lob
	private String sex;

	/**
	* 是否有经验，即是否做过微商
	* @remark 0-否；1-是
	*/
	private String hasExperience;

	private String openid;

	private String unionid;

	/**
	* 上级AgentID
	*/
	private Integer leaderId;

	/**
	* 上级代理姓名
	*/
	private String leaderName;

	/**
	* 上级代理电话
	*/
	private String leaderPhone;

}
