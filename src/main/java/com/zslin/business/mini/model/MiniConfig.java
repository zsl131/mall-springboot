package com.zslin.business.mini.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 小程序配置
 * @author 钟述林
 * @data generate on: 2019-12-09
 */
@Data
@Entity
@Table(name = "mini_mini_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MiniConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
	
	/**
	* 小程序Appid
	*/
	private String appid;

	/**
	* 小程序密钥
	*/
	private String appSecret;

	/**
	* 请求地址
	*/
	private String requestUrl;

	/**
	* 上传地址
	*/
	private String uploadUrl;

	/**
	* 下载地址
	*/
	private String downloadUrl;

	/**
	* 消息推送地址
	*/
	private String msgUrl;

	/**
	* 消息推送Token
	*/
	private String msgToken;

	/**
	* 消息推送AESKey
	*/
	private String msgAesKey;

}
