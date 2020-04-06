package com.zslin.business.mini.tools;

import com.zslin.core.tools.RandomTools;
import com.zslin.core.tools.SecurityUtil;
import sun.security.provider.MD5;

import java.security.NoSuchAlgorithmException;

/**
 * 微信支付常用工具
 */
public class PayUtils {

    /**
     * 获取32位的随机数
     * @return
     */
    private static String getNonceStr() {
        return RandomTools.randomString(32).toUpperCase();
    }

    public static String buildSign(String appid, String mchid, String body, String apiKey, String nonceStr) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("appid=").append(appid).append("&body=").append(body)
                    .append("&device_info=WEB&mch_id=").append(mchid)
                    .append("&nonce_str=").append(nonceStr);
            sb.append("&key=").append(apiKey);
            System.out.println(sb.toString());
            String sign = SecurityUtil.md5(sb.toString()).toUpperCase();
            return sign;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @param appid
     * @param mchid
     * @param body 即支付标题
     * @param apiKey 商户API密钥
     * @return
     */
    public static String buildSignXml(String appid, String mchid, String body, String apiKey) {
        String nonceStr = getNonceStr();
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        sb.append("<appid><![CDATA[").append(appid).append("]]></appid>")
            .append("<body><![CDATA[").append(body).append("]]></body>")
            .append("<device_info><![CDATA[WEB]]></device_info>")
            .append("<mch_id><![CDATA[").append(mchid).append("]]></mch_id>")
            .append("<nonce_str><![CDATA[").append(nonceStr).append("]]></nonce_str>")
            .append("<sign>").append(buildSign(appid, mchid, body, apiKey, nonceStr)).append("</sign>");
        sb.append("</xml>");
        return sb.toString();
    }
}