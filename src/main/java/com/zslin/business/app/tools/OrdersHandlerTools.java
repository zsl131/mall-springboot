package com.zslin.business.app.tools;

import com.zslin.business.app.dto.SubmitOrdersDto;
import com.zslin.business.app.dto.orders.OrdersHandlerDto;
import com.zslin.business.app.dto.orders.OrdersProductDto;
import com.zslin.business.app.dto.orders.OrdersRateDto;
import com.zslin.business.dao.*;
import com.zslin.business.dto.OrdersShowDto;
import com.zslin.business.model.*;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.business.wx.tools.WxAccountTools;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.WxCustomDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 订单处理工具类
 *
 */
@Component("ordersHandlerTools")
@HasTemplateMessage
public class OrdersHandlerTools {

    @Autowired
    private ICustomAddressDao customAddressDao;

    @Autowired
    private ICustomCouponDao customCouponDao;

    @Autowired
    private IProductDao productDao;

    @Autowired
    private IProductSpecsDao productSpecsDao;

    @Autowired
    private IOrdersDao ordersDao;

    @Autowired
    private IOrdersProductDao ordersProductDao;

    @Autowired
    private IAgentDao agentDao;

    @Autowired
    private IOrdersCouponDao ordersCouponDao;

    @Autowired
    private IAgentLevelDao agentLevelDao;

    @Autowired
    private ICustomCommissionRecordDao customCommissionRecordDao;

    @Autowired
    private RateTools rateTools;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @Transactional
    @TemplateMessageAnnotation(name = "订单创建成功通知", keys = "订单号-商品数量-商品金额")
    public void addOrders(WxCustomDto custom, SubmitOrdersDto ordersDto) {
        Orders oldOrders = ordersDao.findByOrdersKey(ordersDto.getOrdersKey());
        if(oldOrders!=null) {return;} //如果订单已经存在，则不能再操作
//        System.out.println(custom);
//        System.out.println("------------------------------");
        //SubmitOrdersDto(ordersKey=1_442098271, addressId=5, agentId=0 couponId=0, remark=, productData=_23-89-8_20-82-3_)
        CustomCoupon coupon = null; //优惠券
        CustomAddress address = null; //收货地址
        Agent agent = null; //对应代理
        AgentLevel level = null; //代理对应的代理等级对象
        if(ordersDto.getCouponId()!=null && ordersDto.getCouponId()>0) {coupon = customCouponDao.findOne(ordersDto.getCouponId());}
        if(ordersDto.getAddressId()!=null && ordersDto.getAddressId()>0) {address = customAddressDao.findOne(ordersDto.getAddressId());}
//        if(ordersDto.getAgentId()!=null && ordersDto.getAgentId()>0) {agent = agentDao.findOne(ordersDto.getAgentId());} //由于前端获取的是Customer，所以不能通过agentId获取对象
        if(ordersDto.getAgentOpenid()!=null && !"".equals(ordersDto.getAgentOpenid())) {agent = agentDao.findByOpenid(ordersDto.getAgentOpenid());} //通过Openid获取对象
        if(agent!=null && agent.getLevelId()!=null && agent.getLevelId()>0) {level = agentLevelDao.findOne(agent.getLevelId());}

        String ordersKey = ordersDto.getOrdersKey();
        String ordersNo = buildOrdersNo(custom.getCustomId());
        //产品信息列表
        List<OrdersProductDto> productDtoList = generateProducts(ordersDto.getProductData());
        List<CustomCommissionRecord> commissionRecordList = buildCommission(agent, custom, level, productDtoList, ordersKey, ordersNo);

        OrdersHandlerDto countDto = buildHandlerDto(productDtoList, commissionRecordList);

//        System.out.println(ordersDto);
        Orders orders = addOrders(ordersKey, ordersNo, custom, address, agent, coupon, countDto, ordersDto.getRemark());
        //订单生成后要处理用户优惠券
        buildCoupon(orders, coupon);
        //保存佣金，
        for(CustomCommissionRecord ccr : commissionRecordList) {
            ccr.setOrdersId(orders.getId());
            customCommissionRecordDao.save(ccr);
        }
        //保存订单产品
        saveOrderProducts(orders, agent, level, custom, productDtoList);

        sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "订单创建成功通知", "", "有顾客下单单了",
                TemplateMessageTools.field("订单号", ordersNo),
                TemplateMessageTools.field("商品数量", orders.getTotalCount()+" 件"),
                TemplateMessageTools.field("商品金额", orders.getTotalMoney()+" 元"),

                TemplateMessageTools.field("可以前往后台管理系统查看"));
    }

    private Orders addOrders(String ordersKey, String ordersNo, WxCustomDto custom, CustomAddress address,
                           Agent agent, CustomCoupon coupon, OrdersHandlerDto countDto, String remark) {
        Orders order = new Orders();
        order.setAddressCon(buildAddressCon(address));
        order.setAddressId(address.getId());
        order.setRemark(remark);
        order.setHasAgent("0"); //默认为没有代理

        if(agent!=null) { //如果有代理信息，则设置
            order.setAgentName(agent.getName());
            order.setAgentOpenid(agent.getOpenid());
            order.setAgentPhone(agent.getPhone());
            order.setAgentUnionid(agent.getUnionid());
            order.setAgentId(agent.getId());
            order.setHasAgent("1"); //设置为有代理
        }
        order.setCreateDay(NormalTools.curDate());
        order.setCreateTime(NormalTools.curDatetime());
        order.setCreateLong(System.currentTimeMillis());
        order.setCustomId(custom.getCustomId());
        order.setOpenid(custom.getOpenid());
        order.setUnionid(custom.getUnionid());
        order.setNickname(custom.getNickname());
        order.setHeadImgUrl(custom.getHeadImgUrl());
        order.setStatus("0");
        order.setHasAfterSale("0"); //默认为无售后问题
        order.setOrdersKey(ordersKey);
        order.setOrdersNo(ordersNo);
        order.setFreight(0f); //TODO 设置运费
        if(coupon!=null) {
            order.setDiscountMoney(coupon.getWorth()); //TODO 设置优惠金额
            order.setDiscountReason(coupon.getCouponName()); //TODO 设置优惠原因
        }
        order.setFundMoney(countDto.getFundMoney()); //TODO 设置基金金额
        order.setSpecsCount(countDto.getSpecsCount()); //TODO 设置产品件数
        order.setTotalCommission(countDto.getTotalCommission()); //TODO 设置佣金金额
        order.setTotalCount(countDto.getTotalCount()); //TODO 设置产品总数量
        order.setTotalMoney(countDto.getTotalMoney()); //TODO 设置总金额
        ordersDao.save(order);
        return order;
    }

    /**
     * 构建计数DTO
     * @param dtoList
     * @param commissionRecordList
     * @return
     */
    private OrdersHandlerDto buildHandlerDto(List<OrdersProductDto> dtoList, List<CustomCommissionRecord> commissionRecordList) {
        /** 基金金额 */
        Float fundMoney=0f;
        /** 总件数 */
        Integer specsCount=0;
        /** 总佣金金额 */
        Float totalCommission=0f;
        /** 产品总数量 */
        Integer totalCount=0;
        /** 总金额 */
        Float totalMoney=0f;
        List<Integer> proIdsList = new ArrayList<>();
        for(OrdersProductDto dto:dtoList) {
            if(!proIdsList.contains(dto.getProduct().getId())) {proIdsList.add(dto.getProduct().getId());}
            specsCount += dto.getAmount();
            totalMoney += dto.getSpecs().getPrice()*dto.getAmount();
            fundMoney += (dto.getProduct().getFund()*dto.getAmount());//
        }
        totalCount = proIdsList.size();
        for(CustomCommissionRecord ccr : commissionRecordList) {
            totalCommission += ccr.getMoney();
        }

        return new OrdersHandlerDto(fundMoney, specsCount, totalCommission, totalCount, totalMoney);
    }

    private void saveOrderProducts(Orders order, Agent agent, AgentLevel level, WxCustomDto custom, List<OrdersProductDto> productDtoList) {
        for(OrdersProductDto dto:productDtoList) {
            Product pro = dto.getProduct();
            OrdersProduct op = new OrdersProduct();
            if(level!=null) {
                op.setAgentLevelId(level.getId());
                op.setAgentLevelName(level.getName());
            }
            if(agent!=null) {
                op.setAgentOpenid(agent.getOpenid());
                op.setAgentUnionid(agent.getUnionid());
                op.setAgentId(agent.getId());
            }
            op.setAmount(dto.getAmount());
            op.setCustomId(custom.getCustomId());
            op.setDeliveryDate(pro.getDeliveryDate());
            op.setFund(pro.getFund()*dto.getAmount());
            op.setHasAfterSale("0");
            op.setNickname(custom.getNickname());
            op.setOpenid(custom.getOpenid());
            op.setOrdersId(order.getId());
            op.setOrdersKey(order.getOrdersKey());
            op.setOrdersNo(order.getOrdersNo());
            op.setOriPrice(dto.getSpecs().getOriPrice());
            op.setPrice(dto.getSpecs().getPrice());
            op.setProId(pro.getId());
            op.setProTitle(pro.getTitle());
            op.setSaleMode(pro.getSaleMode());
            op.setProImg(pro.getHeadImgUrl()); //图片
            op.setSpecsId(dto.getSpecs().getId());
            op.setSpecsName(dto.getSpecs().getName());
            op.setUnionid(custom.getUnionid());
            ordersProductDao.save(op); //保存
            productDao.plusSaleCount(dto.getAmount(), pro.getId()); //增加销量
            minusSpecsCount(dto.getSpecs().getId(), dto.getAmount()); //减少库存
        }
    }

    /**
     * 减库存
     * @param specsId 规格ID
     * @param amount 数量
     */
    private void minusSpecsCount(Integer specsId, Integer amount) {
        productSpecsDao.minusAmount(amount, specsId);
    }

    /**
     * //TODO 除了当级代理的佣金还有上级代理的佣金
     * 构建佣金记录
     * 添加时要遍历设置ordersId
     * @param agent 代理信息
     * @param custom 客户信息
     * @param level 代理等级
     * @param proDtoList 产品对象列表
     * @param ordersKey 订单Key
     * @param ordersNo 订单编号
     * @return
     */
    private List<CustomCommissionRecord> buildCommission(Agent agent, WxCustomDto custom, AgentLevel level, List<OrdersProductDto> proDtoList, String ordersKey, String ordersNo) {
        List<CustomCommissionRecord> result = new ArrayList<>();
        if(agent==null || level==null) {return result;}
        Agent leaderAgent = null;
        if(agent.getLeaderId()!=null && agent.getLeaderId()>0) {leaderAgent = agentDao.findOne(agent.getLeaderId());} //获取上级代理
        for(OrdersProductDto proDto:proDtoList) {
            OrdersRateDto rateDto = rateTools.getRate(level.getId(), proDto.getSpecs().getId()); //佣金DTO对象
            result.add(buildRecord(agent, level, custom, rateDto.getThisAmount(), ordersKey, ordersNo, proDto));
            if(leaderAgent!=null) { //如果有上级代理，也添加进去
                result.add(buildRecord(leaderAgent, level, custom, rateDto.getLeaderAmount(), ordersKey, ordersNo, proDto));
            }
        }
        return result;
    }

    private CustomCommissionRecord buildRecord(Agent agent, AgentLevel level, WxCustomDto custom, Float money,
                                               String ordersKey, String ordersNo, OrdersProductDto proDto) {
        CustomCommissionRecord ccr = new CustomCommissionRecord();
        ccr.setAgentId(agent.getId());
        ccr.setAgentLevelId(level.getId());
        ccr.setAgentLevelName(level.getName());
        ccr.setAgentName(agent.getName());
        ccr.setAgentOpenid(agent.getOpenid());
        ccr.setAgentPhone(agent.getPhone());
        ccr.setAgentUnionid(agent.getUnionid());
        ccr.setCreateDay(NormalTools.curDate());
        ccr.setCreateLong(System.currentTimeMillis());
        ccr.setCreateTime(NormalTools.curDatetime());
        ccr.setCustomId(agent.getCustomId());
        ccr.setCustomNickname(custom.getNickname());
        ccr.setCustomOpenid(custom.getOpenid());
        ccr.setCustomUnionid(custom.getUnionid());
        ccr.setMoney(money); //TODO 设置佣金
        ccr.setOrdersKey(ordersKey);
        ccr.setOrdersNo(ordersNo);
        ccr.setProId(proDto.getProduct().getId());
        ccr.setProTitle(proDto.getProduct().getTitle());
        ccr.setSpecsId(proDto.getSpecs().getId());
        ccr.setSpecsName(proDto.getSpecs().getName());
        ccr.setStatus("0"); //默认为0，用户刚下单
        return ccr;
    }

    /**
     * 处理优惠券
     * @param order
     * @param coupon
     */
    private void buildCoupon(Orders order, CustomCoupon coupon) {
        if(coupon!=null) {
            OrdersCoupon oc = new OrdersCoupon();
            oc.setCouponId(coupon.getCouponId());
            oc.setCouponName(coupon.getCouponName());
            oc.setDiscountMoney(coupon.getWorth());
            oc.setOpenid(coupon.getOpenid());
            oc.setOrdersKey(order.getOrdersKey());
            oc.setOrdersNo(order.getOrdersNo());
            oc.setOrdersId(order.getId());
            oc.setUnionid(coupon.getUnionid());
            oc.setUsedDay(NormalTools.curDate());
            oc.setUsedLong(System.currentTimeMillis());
            oc.setUsedTime(NormalTools.curDatetime());
            oc.setCustomCouponId(coupon.getId());
            ordersCouponDao.save(oc);
            customCouponDao.updateStatus("3", coupon.getId()); //设置为已使用
        }
    }

    private List<OrdersProductDto> generateProducts(String productData) {
        List<OrdersProductDto> result = new ArrayList<>();
        String [] array = productData.split("_");
        for(String str : array) {
            if(str!=null && !"".equals(str.trim())) {
                Integer [] ids = getIds(str); //0-产品ID；1-规格ID；2-数量
                if(ids!=null) {
                    Product product = productDao.findOne(ids[0]);
                    ProductSpecs specs = productSpecsDao.findOne(ids[1]); //产品规格
                    Integer amount = ids[2]; //数量
                    result.add(new OrdersProductDto(product, specs, amount));
                }
            }
        }
        return result;
    }

    private Integer [] getIds(String str) {
        String [] ids = str.split("-");
        if(ids.length==3) {
            return new Integer[]{Integer.parseInt(ids[0]), Integer.parseInt(ids[1]), Integer.parseInt(ids[2])};
        } else {return null;} //如果长度不够则过虑；
    }

    private String buildAddressCon(CustomAddress address) {
        StringBuffer sb = new StringBuffer();
        sb.append(address.getName()).append(",")
                .append(address.getProvinceName())
                .append(address.getCityName())
                .append(address.getCountyName())
                .append(address.getStreet()).append(",")
                .append(address.getPhone());
        return sb.toString();
    }

    /**
     * 订单编号规则
     * 前14位是时间，后3位是随机数，中间数字为用户ID
     * @param customId 用户ID
     * @return
     */
    public String buildOrdersNo(Integer customId) {
        String curDate = NormalTools.getNow("yyyyMMddHHmmss")+customId;
        int random = genRandomInt();
        return  curDate+random;
    }

    private Integer genRandomInt() {
        int res = 0;
        Random ran = new Random();
        while(res<100) {
            res = ran.nextInt(999);
        }
        return res;
    }

    /**
     * 为了方便显示，重新构建订单列表
     * @param ordersList
     * @return
     */
    public List<OrdersShowDto> rebuildOrders(List<Orders> ordersList) {
        List<OrdersShowDto> result = new ArrayList<>();
        for(Orders orders : ordersList) {
            result.add(new OrdersShowDto(orders, ordersProductDao.findByOrdersId(orders.getId()),
                    customCommissionRecordDao.findByOrdersId(orders.getId())));
        }
        return result;
    }
}

