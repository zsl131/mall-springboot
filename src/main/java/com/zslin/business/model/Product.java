package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 产品信息
 * @author 钟述林
 * @data generate on: 2019-12-09
 */
@Data
@Entity
@Table(name = "business_product")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
	
	/**
	* 产品名称
	* @remark 如：昭通糖心苹果
	*/
	private String title;

	/**
	* 产品图文内容
	*/
	@Lob
	private String content;

	/**
	* 浏览次数
	*/
	private Integer readCount=0;

	/**
	* 评论次数
	*/
	private Integer replyCount=0;

	/**
	* 库存数量
	* @remark 这个数量只用于判断是否可购买
	*/
	private Integer surplusCount=0;

	/**
	* 显示状态
	* @remark 0-隐藏；1-显示
	*/
	private String status;

	/**
	* 收藏次数
	*/
	private Integer favoriteCount=0;

	/**
	* 点赞次数
	*/
	private Integer heartCount=0;

	/**
	* 已售数量
	* @remark 前台显示应使用：saleCount+defaultSale
	*/
	private Integer saleCount=0;

	/**
	* 默认销售量
	* @remark 此数值仅用于前期销售量的显示
	*/
	private Integer defaultSale=0;

	/**
	* 主图地址
	* @remark 用于在列表页中显示
	*/
	private String headImgUrl;

	/**
	* 销售类型
	* @remark 0-未上架；1-正常销售；2-预售
	*/
	private String saleMode;

	/**
	* 售价
	* @remark 只用于在列表页中显示，实际价格见'产品规格'
	*/
	private Float price;

	/**
	* 公益基金金额
	* @remark 每个产品统一一个金额，为0时不显示
	*/
	private Float fund;

	/**
	* 归属省份代码
	*/
	private String provinceCode;

	/**
	* 归属省份名称
	*/
	private String provinceName;

	/**
	* 归属市级代码
	*/
	private String cityCode;

	/**
	* 归属市级名称
	*/
	private String cityName;

	/**
	* 归属县级代码
	*/
	private String countyCode;

	/**
	* 归属县级名称
	*/
	private String countyName;

}
