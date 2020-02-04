package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;


/**
 * 代理
 * @author 钟述林
 * @data generate on: 2020-02-04
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
	* 昵称
	*/
	private String nickname;

	/**
	* 状态
	* @remark 0-申请；1-正式代理；2-驳回申请；
	*/
	private String status="0";

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
	* 街道地址
	*/
	private String street;

	/**
	* 小程序选择地址的序号
	* @remark 如：0-0-1，表示第一个省；第一个市；第二个县区
	*/
	private String addressIndex;

	/**
	* 性别
	* @remark 1-男；2-女
	*/
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

	/**
	* 证件数量
	* @remark 对应AgentPaper
	*/
	private Integer paperCount=0;

	/**
	* 审核次数
	*/
	private Integer verifyCount=0;

	/**
	* 级别调整次数
	*/
	private Integer relationCount=0;

	/**
	* 下级代理人数
	*/
	private Integer subCount=0;

	/**
	* 订单数量
	*/
	private Integer ordersCount=0;

	/**
	* 当前等级ID
	*/
	private Integer levelId;

	/**
	* 当前等级名称
	*/
	private String levelName;

}
