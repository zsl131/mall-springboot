package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 移动端首页大功能展示
 * @author 钟述林
 * @data generate on: 2019-12-02
 */
@Data
@Entity
@Table(name = "business_app_module")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppModule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
	
	/**
	* 序号
	*/
	private Integer orderNo;

	/**
	* 图标
	*/
	private String icon;

	/**
	* 名称（4个字）
	*/
	private String name;

	/**
	* 背景色
	* @remark 如：#34CD6D
	*/
	private String bgColor;

	/**
	* 跳转链接地址
	*/
	private String url;

	/**
	* 显示状态
	* @remark 0-隐藏；1-显示
	*/
	private String status;

}
