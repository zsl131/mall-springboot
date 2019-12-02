package com.zslin.business.mini.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.mini.dao.IMiniConfigDao;
import com.zslin.business.mini.dto.NewCustomDto;
import com.zslin.business.mini.model.MiniConfig;
import com.zslin.business.model.Customer;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.rabbit.RabbitMQConfig;
import com.zslin.core.tools.JsonTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class MiniAuthService {

    @Autowired
    private IMiniConfigDao miniConfigDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public JsonResult getUserInfo(String params) {
        System.out.println(params);
        NewCustomDto dto = JSONObject.toJavaObject(JSON.parseObject(params), NewCustomDto.class);
        String code = JsonTools.getJsonParam(params, "code");
        MiniConfig config = miniConfigDao.loadOne();
        log.info("code:::"+code);
        log.info(config.toString());
        RestTemplate template = new RestTemplate();
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+config.getAppid()
                +"&secret="+config.getAppSecret()+"&js_code="+code+"&grant_type=authorization_code";
        String str = template.getForObject(url, String.class);
        String openid = JsonTools.getJsonParam(str, "openid");
        if(openid!=null && !"".equals(openid)) {
            //System.out.println(str);
            Customer customer = new Customer();
            customer.setHeadImgUrl(dto.getAvatarUrl());
            customer.setNickname(dto.getNickName());
            customer.setOpenid(openid);
            customer.setSex(dto.getGender()==1?"1":"2");
            customer.setStatus("1");
            rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.DIRECT_ROUTING, customer);
            return JsonResult.success("获取成功").set("openid", openid);
        } else {
            log.error("获取openid异常:::: "+str);
            return JsonResult.error("获取Openid异常");
        }
    }
}
