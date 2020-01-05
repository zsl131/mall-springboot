package com.zslin.core.controller.tools;

import com.zslin.core.controller.dto.ApiDto;
import com.zslin.core.tools.Base64Utils;
import com.zslin.core.tools.JsonParamTools;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;

@Component
public class ApiTools implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public ApiDto buildApiDto(HttpServletRequest request, String apiCode) throws NoSuchMethodException, UnsupportedEncodingException {
        String serviceName = apiCode.split("\\.")[0];
        String actionName = apiCode.split("\\.")[1];
//        Object obj = factory.getBean(serviceName);
        Object obj = getApplicationContext().getBean(serviceName);
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

        Class<?> userClass = ClassUtils.getUserClass(obj);
        //method代表接口中的方法，specificMethod代表实现类中的方法
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

        return new ApiDto(specificMethod, obj, hasParams, params);
    }
}
