package com.zslin.business.finance.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

/**
 * 分类
 * Created by zsl on 2019/1/3.
 */
@Entity
@Table(name = "f_finance_category")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinanceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
