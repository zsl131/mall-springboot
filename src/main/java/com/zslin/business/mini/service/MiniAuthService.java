package com.zslin.business.mini.service;

import com.zslin.business.dao.ICustomerDao;
import com.zslin.business.mini.dao.IMiniConfigDao;
import com.zslin.business.mini.dto.NewCustomDto;
import com.zslin.business.mini.model.MiniConfig;
import com.zslin.business.mini.tools.MiniUtils;
import com.zslin.business.model.Customer;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.exception.BusinessException;
import com.zslin.core.exception.BusinessExceptionCode;
import com.zslin.core.qiniu.tools.QiniuTools;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.MyBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@Explain(name = "小程序授权管理", notes = "获取小程序用户信息")
public class MiniAuthService {

    @Autowired
    private IMiniConfigDao miniConfigDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @ExplainOperation(name = "获取微信用户信息", params = {
            @ExplainParam(value = "code", name = "loginCode", require = true, example = "通过uni.login获取"),
            @ExplainParam(value = "encryptedData", name = "encryptedData", require = true, example = "通过uni.getUserInfo获取"),
            @ExplainParam(value = "iv", name = "iv", require = true, example = "通过uni.getUserInfo获取"),
    }, back = {
            @ExplainReturn(field = "custom", notes = "返回已获取的用户对象信息")
    })
    public JsonResult getUserInfo(String params) throws BusinessException {
        //System.out.println(params);
        //NewCustomDto dto = JSONObject.toJavaObject(JSON.parseObject(params), NewCustomDto.class);
        String code = JsonTools.getJsonParam(params, "code");
        String enc = JsonTools.getJsonParam(params, "encryptedData"); //
        String iv = JsonTools.getJsonParam(params, "iv");
        if(NormalTools.isNullOr(code, enc, iv)) {
            throw new BusinessException(BusinessExceptionCode.PARAM_NULL, "code、encryptedData、iv三者均不能为空");
        }

        MiniConfig config = miniConfigDao.loadOne();
        //log.info("code:::"+code);
        //log.info(config.toString());
        RestTemplate template = new RestTemplate();
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+config.getAppid()
                +"&secret="+config.getAppSecret()+"&js_code="+code+"&grant_type=authorization_code";
        String str = template.getForObject(url, String.class);
        //System.out.println("====================================");
        //log.info(str);
        String openid = JsonTools.getJsonParam(str, "openid");
        String sessionKey = JsonTools.getJsonParam(str, "session_key");
        NewCustomDto dto = MiniUtils.decryptionUserInfo(enc, sessionKey, iv);
        if(openid!=null && !"".equals(openid)) {
            //System.out.println(str);
            Customer customer = new Customer();
            customer.setHeadImgUrl(dto.getAvatarUrl());
            customer.setNickname(dto.getNickName());
            customer.setOpenid(openid);
            customer.setSex(dto.getGender()==1?"1":"2");
            customer.setStatus("1");
            customer.setUnionid(dto.getUnionId());
            //rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.DIRECT_ROUTING, customer);

            return JsonResult.success("获取成功").set("custom", handlerCustomer(customer));
        } else {
            log.error("获取openid异常:::: "+str);
            return JsonResult.error("获取Openid异常");
        }
    }

    @Autowired
    private ICustomerDao customerDao;
    @Autowired
    private QiniuTools qiniuTools;
    /** 处理小程序获取用户授权信息 */
    private Customer handlerCustomer(Customer customer) {
        Customer old = customerDao.findByOpenid(customer.getOpenid());
        String headimg = customer.getHeadImgUrl();
        if(customer.getHeadImgUrl()!=null && !"".equals(customer.getHeadImgUrl())) { //如果有头像
            headimg = qiniuTools.uploadCustomerHeadImg(headimg, customer.getUnionid()+".jpg");
        }
        customer.setHeadImgUrl(headimg);
        if(old==null) { //如果不存在
            customer.setFirstFollowDay(NormalTools.curDate());
            customer.setFirstFollowTime(NormalTools.curDatetime());
            customer.setFirstFollowLong(System.currentTimeMillis());
            customer.setFollowDay(NormalTools.curDate());
            customer.setFollowTime(NormalTools.curDatetime());
            customer.setFollowLong(System.currentTimeMillis());
            customerDao.save(customer);
            return customer;
        } else {
            MyBeanUtils.copyProperties(customer, old, "id", "firstFollowDay", "firstFollowTime", "firstFollowLong");
            customer.setFollowDay(NormalTools.curDate());
            customer.setFollowTime(NormalTools.curDatetime());
            customer.setFollowLong(System.currentTimeMillis());
            customerDao.save(old);
            return old;
        }
    }
}
