package com.zslin.business.service;

import com.zslin.business.dao.*;
import com.zslin.business.model.*;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.repository.SpecificationOperator;
import com.zslin.core.tools.ExpressTools;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单物流
 */
@Service
@HasTemplateMessage
public class OrdersExpressService {

    @Autowired
    private IOrdersExpressDao ordersExpressDao;

    @Autowired
    private IExpressCompanyDao expressCompanyDao;

    @Autowired
    private IOrdersDao ordersDao;

    @Autowired
    private ExpressTools expressTools;

    @Autowired
    private IProductDao productDao;

    @Autowired
    private IOrdersProductDao ordersProductDao;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    /** 查询物流详情 */
    public JsonResult queryDetail(String params) {
        Integer id = JsonTools.getId(params);
        OrdersExpress oe = ordersExpressDao.findByOrdersId(id);
        if(canQuery(oe)) { //如果需要重新获取
            if(oe==null) {
                Orders orders = ordersDao.findOne(id);
                oe = new OrdersExpress();
                oe.setOrdersNo(orders.getOrdersNo());
                oe.setOrdersId(id);
                oe.setCustomNickname(orders.getNickname());
                oe.setCustomId(orders.getCustomId());
            }
            String str = expressTools.query(oe.getExpNo());
            oe.setExpCon(str);
            oe.setUpdateTime(NormalTools.curDatetime());
            oe.setUpdateLong(System.currentTimeMillis());
            ordersExpressDao.save(oe);
        }

        QueryTools qt = new QueryTools();
        Page<Product> res = productDao.findAll(qt.buildSearch(new SpecificationOperator("status", "eq", "1", "and"),
                new SpecificationOperator("isRecommend", "eq", "1")),
                SimplePageBuilder.generate(0, 8, SimpleSortBuilder.generateSort("orderNo_a")));

        return JsonResult.success().set("express", oe)
                .set("detail", expressTools.query2DtoByStr(oe.getExpCon()))
                .set("recommendList", res.getContent());
    }

    private boolean canQuery(OrdersExpress oe) {
        if(oe!=null) {
            Long preLong = oe.getUpdateLong();
            Long curLong = System.currentTimeMillis();
            Long diffLong = 5 * 3600 * 1000l; //5个小时
            if(curLong - preLong>=diffLong) { //需要重新获取
                return true;
            } else {return false;}
        } else {return true;}
    }

    /** 获取已经存在的订单物流 */
    public JsonResult onExpress(String params) {
        String ordersNo = JsonTools.getJsonParam(params, "ordersNo");
        List<OrdersExpress> expressList = ordersExpressDao.findByOrdersNo(ordersNo);
        List<ExpressCompany> companyList = expressCompanyDao.findAll();
        List<OrdersProduct> productList = ordersProductDao.findByOrdersNo(ordersNo);

        return JsonResult.success().set("expressList", expressList)
                .set("companyList", companyList).set("productList", productList);
    }

    /** 为订单发货 */
    @TemplateMessageAnnotation(name = "商品发货通知", keys = "快递公司-快递单号-商品信息-商品数量")
    public JsonResult express(String params) {
        String ordersNo = JsonTools.getJsonParam(params, "ordersNo");
        Orders orders = ordersDao.findByOrdersNo(ordersNo);

        String addressCon = JsonTools.getJsonParam(params, "addressCon"); //收货地址
        Integer expId = JsonTools.getParamInteger(params, "expId"); //物流公司ID
//        String expName = JsonTools.getJsonParam(params, "expName"); //物流公司名称
        String expNo = JsonTools.getJsonParam(params, "expNo"); //物流单号
        Integer ordersProId = JsonTools.getParamInteger(params, "proId"); //订单产品对象ID

        String expName = expressCompanyDao.findOne(expId).getName(); //物流公司名称

        String [] expNoArray = handleExpress(expNo);
        OrdersExpress express = null;
        int size = 0;
        for(String no:expNoArray) {
            if(no!=null && ("自提".equals(no) || "送货上门".equals(no) || ordersExpressDao.findByExpNo(no).size()<=0)) {
                express = new OrdersExpress();
                express.setCustomId(orders.getCustomId());
                express.setCustomNickname(orders.getNickname());
                express.setAddressCon(addressCon);
                express.setOpenid(orders.getOpenid());
                express.setOrdersId(orders.getId());
                express.setOrdersNo(ordersNo);
                express.setUpdateLong(System.currentTimeMillis());
                express.setUpdateTime(NormalTools.curDatetime());

                express.setExpId(expId);
                express.setExpName(expName);
                express.setExpNo(buildNo(no, addressCon, expName));
                express.setOrdersProId(ordersProId);

                ordersExpressDao.save(express); //保存
                size ++;
            }
        }

        /*List<OrdersExpress> expressList = ordersExpressDao.findByOrdersNo(ordersNo);
        OrdersExpress express = null;
        if(express==null) {
            express = new OrdersExpress();
            express.setCustomId(orders.getCustomId());
            express.setCustomNickname(orders.getNickname());
            express.setAddressCon(addressCon);
            express.setOpenid(orders.getOpenid());
            express.setOrdersId(orders.getId());
            express.setOrdersNo(ordersNo);
            express.setUpdateLong(System.currentTimeMillis());
            express.setUpdateTime(NormalTools.curDatetime());
        }

        express.setExpId(expId);
        express.setExpName(expName);
        express.setExpNo(buildNo(expNo, addressCon, expName));

        ordersExpressDao.save(express); //保存*/

//        ordersDao.updateStatus("2", ordersNo, orders.getCustomId()); //修改状态为已发货
        orders.setStatus("2");//修改状态为已发货
        orders.setSendDay(NormalTools.curDate());
        orders.setSendTime(NormalTools.curDatetime());
        orders.setSendLong(System.currentTimeMillis());
        orders.setSendCount(orders.getSendCount()+size);
        ordersDao.save(orders);

        //快递公司-快递单号-商品信息-商品数量
        sendTemplateMessageTools.send(orders.getOpenid(), "商品发货通知", "", "您购买的商品已发货啦~",
                TemplateMessageTools.field("快递公司", express==null?"无":express.getExpName()),
                TemplateMessageTools.field("快递单号", express==null?"无":express.getExpNo()),
                TemplateMessageTools.field("商品信息", "-"),
                TemplateMessageTools.field("商品数量", expNoArray.length+" 件"),

                TemplateMessageTools.field("您可以在“满山晴”小程序中查看物流信息"));

        return JsonResult.success("保存成功");
    }

    private String[] handleExpress(String expressNo) {
        expressNo = expressNo.replaceAll(" ", "").replaceAll("，",",");
        return expressNo.split(",");
    }

    //是否是顺丰
    private boolean isSF(String expName) {
        return (expName.contains("顺丰"));
    }

    private String buildNo(String expNo, String addressCon, String expName) {
        if(isSF(expName)) {
            return expNo + ":"+(addressCon.substring(addressCon.length()-4));
        } else {return expNo;}
    }
}
