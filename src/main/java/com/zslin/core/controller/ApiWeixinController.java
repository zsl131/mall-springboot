package com.zslin.core.controller;

import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.exception.BusinessException;
import com.zslin.core.exception.BusinessExceptionCode;
import com.zslin.core.tools.AuthCheckTools;
import com.zslin.core.tools.Base64Utils;
import com.zslin.core.tools.JsonParamTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;

@RestController
@RequestMapping(value = "api/wx")
@Slf4j
public class ApiWeixinController {

    @Autowired
    private BeanFactory factory;

    @Autowired
    private AuthCheckTools authCheckTools;

    @GetMapping(value = "get")
    public JsonResult get(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("auth-token"); //身份认证token
        String openid = request.getHeader("openid"); //微信端传入的Openid
        String nickname = Base64Utils.unPassword(request.getHeader("nickname")); //昵称
        //log.info("openid===>"+openid);
        //log.info("pwd nickname====>"+nickname);
        Long authTime = null;
        try {
            authTime = Long.parseLong(request.getHeader("authTime")); //权限时间
        } catch (Exception e) {
        }
//        logger.info("请求AuthToken：： "+token);
        String apiCode = request.getHeader("api-code"); //接口访问编码
        if(token == null || "".equals(token) || apiCode==null || "".equals(apiCode)) {
            return JsonResult.getInstance().fail("auth-token或api-code为空");
        }
        try {
            String serviceName = apiCode.split("\\.")[0];
            String actionName = apiCode.split("\\.")[1];
            Object obj = factory.getBean(serviceName);
            Method method ;
            boolean hasParams = false;
            String params = request.getParameter("params");
            if(params==null || "".equals(params.trim())) {
                method = obj.getClass().getMethod(actionName);
            } else {
                params = Base64Utils.getFromBase64(params);
                params = URLDecoder.decode(params, "utf-8");
//                System.out.println("============="+params);

                params = JsonParamTools.rebuildParams(params, request);

                method = obj.getClass().getMethod(actionName, params.getClass());
                hasParams = true;
            }
            JsonResult result;

            //输出的日志，方便查看
            log.info("移动端调用-->"+apiCode+"-->"+params);

            NeedAuth needAuth = method.getDeclaredAnnotation(NeedAuth.class);
            boolean hasAuth = true;
            if(needAuth!=null && needAuth.openid()) { //需要传入Openid
//                logger.info(serviceName+"."+actionName+"，需要权限验证");
//                hasAuth = authCheckTools.hasAuth(token, authTime);
                hasAuth = !NormalTools.isNull(openid); //只要不空就表示有权限
            } else {
                log.info(serviceName+"."+actionName+"， 不需要openid验证");
            }

            if(hasAuth) {
                if(hasParams) {
                    result = (JsonResult) method.invoke(obj, params);
                } else {
                    result = (JsonResult) method.invoke(obj);
                }
            } else {
//                logger.info("需要重新登陆");
                result = JsonResult.getInstance().failLogin("未检测到用户信息");
            }
            return result;
        } catch (NoSuchMethodException e) {
            //e.printStackTrace();
            return JsonResult.getInstance().fail(BusinessExceptionCode.NO_SUCH_METHOD, "未找到接口："+apiCode);
        } catch (IllegalAccessException e) {
//            e.printStackTrace();
            return JsonResult.getInstance().fail(BusinessExceptionCode.ILLEGAL_ACCESS, "接口未公开："+e.getMessage());
        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
            return JsonResult.getInstance().fail(BusinessExceptionCode.ENCODING, "字符编码异常");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            try {
                BusinessException exc = (BusinessException) e.getTargetException();
                return JsonResult.getInstance().fail(exc.getCode(), exc.getMsg());
            } catch (Exception ex) {
                return JsonResult.getInstance().fail("数据请求失败："+e.getMessage());
            }
        }
    }
}
