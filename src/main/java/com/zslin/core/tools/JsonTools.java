package com.zslin.core.tools;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by zsl on 2018/7/7.
 */
public class JsonTools {

    /**
     * 将String数据格式化成JSONObject对象
     * @param str JSON数据格式的String
     * @return
     */
    public static JSONObject str2JsonObj(String str)  {
        try {
            JSONObject jsonObj = JSONObject.parseObject(str);
            return jsonObj;
        } catch (Exception e) {
//			throw new AppException("JSON数据格式化异常", AppConstant.ExceptionCode.JSON_FORMAT_EXCEPTION);
            return null;
        }
    }

    /**
     * 将Stering数据格式化成JSONArray对象
     * @param str JSON数据格式的String
     * @return
     */
    public static JSONArray str2JsonArray(String str) {
        try {
            JSONArray jsonArray = JSONArray.parseArray(str);
            return jsonArray;
        } catch (Exception e) {
//			throw new AppException("JSON数据格式化异常", AppConstant.ExceptionCode.JSON_FORMAT_EXCEPTION);
            return null;
        }
    }

    /**
     * 获取JSON数据中的某属性值
     * @param jsonStr JSON字符串
     * @param field 字段
     * @return 返回对应的值，如果返回null则表示没有具属性
     */
    public static String getJsonParam(String jsonStr, String field) {
        String result = null;
        try {
            if(jsonStr.startsWith("[")) {
                jsonStr = jsonStr.substring(1, jsonStr.length()-1);
            }
            JSONObject jsonObj = JSONObject.parseObject(jsonStr);
            Object obj = jsonObj.get(field);
            if(obj!=null) {result = obj.toString();}
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 获取boolean属性
     * @param jsonStr json数据
     * @param field 对应属性名
     * @return
     */
    public static boolean getParamBoolean(String jsonStr, String field) {
        try {
            String result = getJsonParam(jsonStr, field);
            boolean res = Boolean.parseBoolean(result);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取Integer属性
     * @param jsonStr json数据
     * @param field 对应属性名
     * @return
     */
    public static Integer getParamInteger(String jsonStr, String field) {
        try {
            String result = getJsonParam(jsonStr, field);
            Integer res = Integer.parseInt(result);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Integer getId(String jsonStr) {
        try {
            return Integer.parseInt(JsonTools.getJsonParam(jsonStr, "id"));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取headerParams中的数据
     * @param params
     * @param field
     * @return
     */
    public static String getHeaderParams(String params, String field) {
        try {
            String headerParams = getJsonParam(params, "headerParams");
            String res = getJsonParam(headerParams, field);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取openid
     * @param params
     * @return
     */
    public static String getOpenid(String params) {
        String openid = getHeaderParams(params, "openid");
        return openid;
    }

    /**
     * 获取Nickname
     * @param params
     * @return
     */
    public static String getNickname(String params) {
        return getHeaderParams(params, "nickname");
    }
}
