package com.zslin.core.controller;

import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.exception.BusinessException;
import com.zslin.core.tools.AuthCheckTools;
import com.zslin.core.tools.Base64Utils;
import com.zslin.core.tools.JsonParamTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
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
import java.util.Enumeration;

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

        String token = request.getHeader("authToken"); //身份认证token
        Long authTime = null;
        try {
            authTime = Long.parseLong(request.getHeader("authTime")); //权限时间，单位秒
        } catch (Exception e) {
        }

        String apiCode = request.getHeader("apiCode"); //接口访问编码

        if(apiCode==null || "".equals(apiCode)) {
            return JsonResult.getInstance().fail("apiCode不能为空");
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

            //输出的日志，方便查看
            log.info("接口调用，apiCode: {}, params: {}", apiCode, params);

            JsonResult result;

            NeedAuth needAuth = method.getDeclaredAnnotation(NeedAuth.class);
            //System.out.println("---------->needAuth:::"+needAuth);
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
                //System.out.println("-------->apiCode:" + apiCode);
                result = JsonResult.getInstance().failLogin("无权访问，请先登陆");
            }
            return result;
        } catch(ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(BusinessException.Code.API_ERR_FORMAT, BusinessException.Message.API_ERR_FORMAT);
        } catch (NoSuchBeanDefinitionException e) {
            return JsonResult.getInstance().fail(BusinessException.Code.NO_BEAN_DEF, BusinessException.Message.NO_BEAN_DEF);
        } catch (NoSuchMethodException e) {
            //e.printStackTrace();
            return JsonResult.getInstance().fail(BusinessException.Code.NO_SUCH_METHOD, BusinessException.Message.NO_SUCH_METHOD);
        } catch (IllegalAccessException e) {
//            e.printStackTrace();
            return JsonResult.getInstance().fail(BusinessException.Code.ILLEGAL_ACCESS, BusinessException.Message.ILLEGAL_ACCESS);
        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
            return JsonResult.getInstance().fail(BusinessException.Code.ENCODING, BusinessException.Message.ENCODING);
        } catch (InvocationTargetException e) {
            try {
                BusinessException exc = (BusinessException) e.getTargetException();
                return JsonResult.getInstance().fail(exc.getCode(), "异常："+exc.getMsg());
            } catch (Exception ex) {
                ex.printStackTrace();
                return JsonResult.getInstance().fail("数据请求失败："+ex.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail("出现异常"+e.getMessage());
        }
    }
}
