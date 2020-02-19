package com.zslin.business.app.dto;

import lombok.Data;

/**
 * 直接购物的DTO对象
 */
@Data
public class ProductSpecsDto {

    //唯一标识
    private String key;

    /** basket、direct */
    private String type;

    private Integer proId;

    private String proImg;

    private String proTitle;

    private Integer specsId;

    private String specsName;

    private Float price;

    private Integer amount;
}
