package com.zslin.business.wx.tools;

import com.alibaba.fastjson.JSONObject;
import com.zslin.core.exception.BusinessException;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 微信素材管理
 */
@Component
public class WxMediaTools {

    @Autowired
    private WxAccessTokenTools wxAccessTokenTools;

    public String queryMedias(Integer offset, Integer count) {
        String url = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token="+wxAccessTokenTools.getAccessToken();
        JSONObject jsonObj = WeixinUtil.httpRequest(url, "POST", buildJson(offset, count));
        //System.out.println(jsonObj);
        return jsonObj.toJSONString();
        /*String code = JsonTools.getJsonParam(jsonObj.toString(), "errcode");
        if(!"0".equals(code)) {
            throw new BusinessException(code, JsonTools.getJsonParam(jsonObj.toJSONString(), "errmsg"));
        }*/
    }

    private String buildJson(Integer offset, Integer count) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("\"type\":news")
                .append("\"offset\":0")
                .append("\"count\":20");
        sb.append("}");
        return sb.toString();
    }
}
