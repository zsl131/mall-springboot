package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 产品标签
 * @author 钟述林
 * @data generate on: 2019-12-02
 */
@Data
@Entity
@Table(name = "business_product_tag")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductTag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
	
	/**
	* 标签名称
	*/
	private String name;

}
