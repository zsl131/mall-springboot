package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 产品分类
 * @author 钟述林
 * @data generate on: 2019-12-09
 */
@Data
@Entity
@Table(name = "business_product_category")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductCategory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
	
	/**
	* 名称
	* @remark 如：苹果
	*/
	private String name;

	/**
	* 父id
	*/
	private Integer pid;

	/**
	* 父名称
	* @remark 如：水果
	*/
	private String pname;

}
