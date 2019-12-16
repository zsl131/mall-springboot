package com.zslin.core.exception;

/**
 * 业务异常代码
 */
public class BusinessExceptionCode {

    /** 参数为空 */
    public static final String PARAM_NULL = "10001";

    /** 系统配置为空 */
    public static final String CONFIG_NULL = "10002";

    /** 未找到接口 */
    public static final String NO_SUCH_METHOD = "20001";

    /** 无法调用private方法 */
    public static final String ILLEGAL_ACCESS = "20002";

    /** 字符编码异常 */
    public static final String ENCODING = "20003";

    /** 未找到Bean */
    public static final String NO_BEAN_DEF = "20004";

    /** 接口格式错误 */
    public static final String API_ERR_FORMAT = "20005";
}
