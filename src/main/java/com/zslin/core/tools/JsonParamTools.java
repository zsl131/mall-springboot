package com.zslin.core.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by zsl on 2018/8/10.
 */
public class JsonParamTools {

    private static final String HEADER_PARAM_NAME = "headerParams";

    public static String rebuildParams(String params, HttpServletRequest request) throws Exception {

        List<String> ignoreNames = new ArrayList<>();

        ignoreNames.add("accept-language");
        ignoreNames.add("accept-encoding");
        ignoreNames.add("referer");
        ignoreNames.add("accept");
        ignoreNames.add("auth-token");
        ignoreNames.add("user-agent");
        ignoreNames.add("api-code");
        ignoreNames.add("connection");
        ignoreNames.add("host");

        Enumeration<String> names = request.getHeaderNames();
        Map<String, Object> headerMap = new HashMap<>();
        while(names.hasMoreElements()) {
            String name = names.nextElement();
            if(!ignoreNames.contains(name)) {
                headerMap.put(name, request.getHeader(name));
            }
        }

        JSONObject jsonObj = JSON.parseObject(params);
        if(!headerMap.isEmpty()) {
            jsonObj.put(HEADER_PARAM_NAME, headerMap);
        }
        String result = jsonObj.toJSONString();
        return result;
    }

    public static String getHeaderParams(String params) {
        return JsonTools.getJsonParam(params, HEADER_PARAM_NAME);
    }

    public static Map<String, String> getHeaderMap(String params) {
        Map<String, String> result = new HashMap<>();
        try {
            JSONObject jsonObj = JSON.parseObject(getHeaderParams(params));
            Iterator<String> keys = jsonObj.keySet().iterator();
            while(keys.hasNext()) {
                String key = keys.next();
                result.put(key, jsonObj.getString(key));
            }
        } catch (Exception e) {
        }
        return result;
    }
}
