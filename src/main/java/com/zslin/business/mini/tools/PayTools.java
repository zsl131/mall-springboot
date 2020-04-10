package com.zslin.business.mini.tools;

import com.github.wxpay.sdk.MyPayConfig;
import com.github.wxpay.sdk.WXPay;
import com.zslin.business.dao.IOrdersDao;
import com.zslin.business.mini.dao.IUnifiedOrderDao;
import com.zslin.business.mini.dto.PaySubmitDto;
import com.zslin.business.mini.model.MiniConfig;
import com.zslin.business.mini.model.UnifiedOrder;
import com.zslin.business.model.Orders;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.tools.ConfigTools;
import com.zslin.core.tools.RandomTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付工具类
 */
@Component
@Slf4j
public class PayTools {

    @Autowired
    private MiniConfigTools miniConfigTools;

    @Autowired
    private IOrdersDao ordersDao;

    private final String BODY_PRE = "满山晴";

    @Autowired
    private ConfigTools configTools;

    @Autowired
    private IUnifiedOrderDao unifiedOrderDao;

    /**
     * 统一下单接口
     * @return 返回一个可以直接到小程序进行支付的DTO对象
     */
    public PaySubmitDto unifiedOrder(WxCustomDto customDto, String ip, String ordersNo) {
        UnifiedOrder resOrder = new UnifiedOrder();
        //Map resultMap=new HashMap();
        Orders orders = ordersDao.findByOrdersNo(ordersNo);
        //获取微信小程序配置文件
        MiniConfig config = miniConfigTools.getMiniConfig();
        Map<String, String> data = new HashMap<>();

        String appId = config.getAppid();
        String apiKey = config.getApiKey();

        String nonceStr = RandomTools.randomString(32);
        String body = BODY_PRE+"-"+orders.getTotalCount()+" 件产品"; //支付名称
        String sign = PayUtils.buildSign(appId, config.getMchid(), body, apiKey, nonceStr);
        data.put("appid", appId);
        data.put("mch_id", config.getMchid());
        data.put("nonce_str", nonceStr);
        data.put("body", body);
        data.put("out_trade_no",ordersNo);
        data.put("total_fee", buildTotalMoney(orders.getTotalMoney()));
        data.put("spbill_create_ip", ip);
        data.put("notify_url", config.getPayNotifyUrl()); //支付结果通知地址
        data.put("trade_type","JSAPI"); //交易类型，小程序填：JSAPI
        data.put("openid", customDto.getOpenid());
        data.put("sign", sign);

        resOrder.setCustomId(customDto.getCustomId());
        resOrder.setHeadImgUrl(customDto.getHeadImgUrl());
        resOrder.setNickname(customDto.getNickname());
        resOrder.setOpenid(customDto.getOpenid());
        resOrder.setOrdersId(orders.getId());
        resOrder.setOrdersNo(orders.getOrdersNo());

        try {

            String certPath = configTools.getFilePath("cert") + "apiclient_cert.p12";

            MyPayConfig payConfig = new MyPayConfig(certPath, config);
            WXPay wxpay = new WXPay(payConfig);

            Map<String, String> rMap = wxpay.unifiedOrder(data);
            System.out.println("统一下单接口返回: " + rMap);
            //log.info(rMap.toString()); //显示结果
            String return_code = rMap.get("return_code");
            String result_code = rMap.get("result_code");
            /*resultMap.put("nonceStr", nonceStr);*/
            //Long timeStamp = System.currentTimeMillis() / 1000;
            if ("SUCCESS".equals(return_code) && return_code.equals(result_code)) {
                String prepayid = rMap.get("prepay_id"); //预支付订单ID

                resOrder.setPrepayId(prepayid);
                resOrder.setStatus("0"); //表示获取成功

                /*resultMap.put("package", "prepay_id="+prepayid);
                resultMap.put("signType", "MD5");
                //这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误
                resultMap.put("timeStamp", timeStamp + "");
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                resultMap.put("appId",config.getAppid());
                resultMap.put("paySign", sign);
                System.out.println("生成的签名paySign : "+ sign);*/
            }else{
//                return  response;
                resOrder.setStatus("-1");
            }
        } catch (Exception e) {
            e.printStackTrace();
//            return  response;
            resOrder.setStatus("-2");
        }

        String status = resOrder.getStatus();
//        log.info(resOrder.toString());
        //在没有出错且prepayId存在时返回DTO
        if("0".equals(status) && resOrder.getPrepayId()!=null && !"".equals(resOrder.getPrepayId())) {
            unifiedOrderDao.save(resOrder); //存入数据库
            return buildSubmitData(appId, resOrder.getPrepayId(), apiKey);
        } else {
            return null;
        }
    }

    /**
     * 生成调起支付的DTO对象
     * @return
     */
    private PaySubmitDto buildSubmitData(String appId, String prepayId, String apiKey) {
        String nonceStr = RandomTools.randomString(32);
        String timestamp = (System.currentTimeMillis() / 1000)+"";
        String sign = PayUtils.buildPaySign(appId, nonceStr, prepayId, timestamp, apiKey);

        PaySubmitDto dto = new PaySubmitDto();
        dto.setAppId(appId);
        dto.setNonceStr(nonceStr);
        dto.setPackageStr("prepay_id="+prepayId);
        dto.setTimeStamp(timestamp);
        dto.setSignType("MD5");
        dto.setPaySign(sign);

        return dto;
    }

    /** 把订单金额换成分 */
    private String buildTotalMoney(Float totalMoney) {
        return String.valueOf((int)(totalMoney*100));
    }
}
