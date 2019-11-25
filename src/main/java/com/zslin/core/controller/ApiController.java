package com.zslin.core.controller;

import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.exception.BusinessException;
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
import java.lang.reflect.Method;
import java.net.URLDecoder;

@RestController
@RequestMapping(value = "api")
@Slf4j
public class ApiController {

    @Autowired
    private BeanFactory factory;

    @Autowired
    private AuthCheckTools authCheckTools;

    @GetMapping(value = "get")
    public JsonResult get(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("auth-token"); //身份认证token
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

            NeedAuth needAuth = method.getDeclaredAnnotation(NeedAuth.class);
            boolean hasAuth = true;
            if(needAuth==null || needAuth.need()) { //需要权限验证
//                logger.info(serviceName+"."+actionName+"，需要权限验证");
                hasAuth = authCheckTools.hasAuth(token, authTime);
            } else {
                log.info(serviceName+"."+actionName+"， 不需要权限验证");
            }

            if(hasAuth) {
                if(hasParams) {
                    result = (JsonResult) method.invoke(obj, params);
                } else {
                    result = (JsonResult) method.invoke(obj);
                }
            } else {
//                logger.info("需要重新登陆");
                result = JsonResult.getInstance().failLogin("无权访问，请先登陆");
            }
            return result;
        } catch (BusinessException e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail("数据请求失败1："+e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail("数据请求失败："+e.getMessage());
        }
    }
}
