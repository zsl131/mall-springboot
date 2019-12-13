package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 通知公告
 * @author 钟述林
 * @data generate on: 2019-12-13
 */
@Data
@Entity
@Table(name = "business_app_notice")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppNotice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 显示内容
	*/
	private String content;

	/**
	* 显示状态
	* @remark 0-隐藏；1-显示
	*/
	private String status;

	/**
	* 打开方式
	* @remark 0-不打开；1-弹窗，显示content;2-打开链接
	*/
	private String openMode;

	/**
	* 跳转链接地址
	*/
	private String url;

}
