package com.zslin.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "base_task")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String taskName;

    private String beanName;

    private String methodName;

    /** 执行规则 */
    private String cron;

    /** 状态，1-启用；0-停用 */
    private String status;

    /** 自动启用，1-是；0-否 */
    private String autoStart;

    /** 参数 */
    @Lob
    private String params;
}
