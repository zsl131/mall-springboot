package com.zslin.business.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.app.dto.ProductSpecsDto;
import com.zslin.business.app.dto.SubmitOrdersDto;
import com.zslin.business.dao.*;
import com.zslin.business.model.*;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.rabbit.RabbitNormalTools;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MiniOrdersService {

    @Autowired
    private IShoppingBasketDao shoppingBasketDao;

    @Autowired
    private ICustomAddressDao customAddressDao;

    @Autowired
    private ICustomCouponDao customCouponDao;

    @Autowired
    private IProductSpecsDao productSpecsDao;

    @Autowired
    private IProductDao productDao;

    @Autowired
    private RabbitNormalTools rabbitNormalTools;

    @NeedAuth(openid = true)
    public JsonResult onPay(String params) {
        JsonResult result = JsonResult.getInstance();
        try {
            String ids = JsonTools.getJsonParam(params, "ids");
            String type = JsonTools.getJsonParam(params, "type"); //类型，direct-直接购买；basket-购物车
            Integer addId = JsonTools.getParamInteger(params, "addressId"); //如果是指定收货地址
            WxCustomDto custom = JsonTools.getCustom(params);
            Integer [] proIds ;
            Float totalMoney = 0f;
            List<ProductSpecs> specsList;
            if("basket".equalsIgnoreCase(type)) { //如果是通过购物车购买
                List<ShoppingBasket> basketList = shoppingBasketDao.findByIds(genBasketIds(ids));
                Integer [] specsIds = buildProSpecsIds(basketList); //产品规则ID
                specsList = productSpecsDao.findByIds(specsIds);
                totalMoney = buildTotalMoney(basketList);
                List<Product> proList = productDao.findByIds(buildProIds(basketList)); //产品列表
                List<ProductSpecsDto> resultList = buildDtoListFromBasket(custom.getOpenid(), rebuildBasket(basketList, specsList), proList);
                proIds = buildProIdsByDto(resultList); //产品ID
                result.set("productList", resultList);
            } else { //如果是直接购买
                Integer specsId = Integer.parseInt(ids);
                ProductSpecs specs = productSpecsDao.findOne(specsId);
                Product pro = productDao.findOne(specs.getProId());
                List<ProductSpecsDto> dtoList = buildDtoList(custom.getOpenid(), specs, pro);

                specsList = new ArrayList<>();
                specsList.add(specs);
                proIds = new Integer[]{specs.getProId()};

                totalMoney = specs.getPrice();

                /*
                List<Product> proList = productDao.findByIds(proIds);*/
                result.set("productList", dtoList);
            }

            CustomAddress address = null;
            if(addId!=null&&addId>0) {
                address = customAddressDao.findByCustomIdAndId(custom.getCustomId(), addId);
            }
            if(address==null) {
                address = customAddressDao.findDefaultAddress(custom.getCustomId()); //默认地址
            }

            List<CustomCoupon> couponList = genCoupon(custom.getCustomId(), totalMoney, proIds); //满足条件的优惠券

            result.set("specsList", specsList).set("address", address)
                .set("totalMoney", totalMoney).set("couponList", couponList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 提交订单
     * @param params
     * @return
     */
    @NeedAuth(openid = true)
    public JsonResult submitOrders(String params) {
        JsonResult result = JsonResult.getInstance();
        WxCustomDto custom = JsonTools.getCustom(params);
        //提交的数据对象
        SubmitOrdersDto objDto = JSONObject.toJavaObject(JSON.parseObject(params), SubmitOrdersDto.class);
        /*Integer addressId = JsonTools.getParamInteger(params, "addressId"); //地址ID
        Integer couponId = JsonTools.getParamInteger(params, "couponId"); //优惠券ID
        String remark = JsonTools.getJsonParam(params, "remark");
        String productData = JsonTools.getJsonParam(params, "productData"); //提交的产品ID，_23-89-8_20-82-3_*/

        rabbitNormalTools.updateData("ordersHandlerTools", "addOrders", custom, objDto);
        return result;
    }

    private List<ProductSpecsDto> buildDtoListFromBasket(String openid, List<ShoppingBasket> basketList, List<Product> proList) {
        List<ProductSpecsDto> result = new ArrayList<>();
        for(ShoppingBasket sb : basketList) {
            Product pro = getPro(proList, sb.getProId()); //获取产品对象
            ProductSpecsDto dto = new ProductSpecsDto();
            dto.setAmount(sb.getAmount());
            dto.setPrice(sb.getPrice());
            dto.setProId(sb.getProId());
            dto.setProImg(sb.getProImg());
            dto.setProTitle(sb.getProTitle());
            dto.setSpecsId(sb.getSpecsId());
            dto.setSpecsName(sb.getSpecsName());
            dto.setKey(buildKey(openid, sb.getSpecsId(), sb.getProId()));
            dto.setType("basket");
            if(pro!=null && "1".equals(pro.getStatus())) { //如果产品为空或status不为1，则表示此产品不存在或隐藏，则不能购买
                dto.setSaleMode(pro.getSaleMode());
                dto.setDeliveryDate(pro.getDeliveryDate());
                result.add(dto);
            }
        }
        return result;
    }

    private Product getPro(List<Product> proList, Integer proId) {
        Product pro = null;
        for(Product p : proList) {
            if(p.getId().equals(proId)) {pro = p; break;}
        }
        return pro;
    }

    private List<ProductSpecsDto> buildDtoList(String openid, ProductSpecs specs, Product pro) {
        List<ProductSpecsDto> result = new ArrayList<>();
        if(pro!=null && "1".equals(pro.getStatus())) {
            ProductSpecsDto dto = new ProductSpecsDto();
            dto.setAmount(1);
            dto.setPrice(specs.getPrice());
            dto.setProId(pro.getId());
            dto.setProImg(pro.getHeadImgUrl());
            dto.setProTitle(pro.getTitle());
            dto.setSpecsId(specs.getId());
            dto.setSpecsName(specs.getName());
            dto.setKey(buildKey(openid, specs.getId(), pro.getId()));
            dto.setSaleMode(pro.getSaleMode());
            dto.setDeliveryDate(pro.getDeliveryDate());
            dto.setType("direct");
            result.add(dto);
        }
        return result;
    }

    /** 生成key */
    private String buildKey(String customOpenid, Integer specsId, Integer proId) {
        try {
            String ids = proId+"-"+specsId;
            String pwd = SecurityUtil.md5(customOpenid, ids);
            return pwd;
        } catch (Exception e) {
            return (Math.random()*1000000)+"";
        }
    }

    /**
     * 重新生成购物车数据，解决购物车产品数量大于产品实际库存的问题
     * @param basketList
     * @param specsList
     * @return
     */
    private List<ShoppingBasket> rebuildBasket(List<ShoppingBasket> basketList, List<ProductSpecs> specsList) {
        List<ShoppingBasket> result = new ArrayList<>();
        for(ShoppingBasket sb: basketList) {
            for(ProductSpecs p : specsList) {
                if(sb.getProId().equals(p.getProId()) && sb.getAmount()>p.getAmount()) {
                    sb.setAmount(p.getAmount());
                }
            }
            if(sb.getAmount()>0) {
                result.add(sb);
            }
        }
        return result;
    }

    /** 统计订单总金额 */
    private Float buildTotalMoney(List<ShoppingBasket> basketList) {
        Float res = 0f;
        for(ShoppingBasket sb : basketList) {
            res += (sb.getPrice()*sb.getAmount());
        }
        return res;
    }

    /** 获取优惠券 */
    private List<CustomCoupon> genCoupon(Integer customId, Float totalMoney, Integer [] proIds) {
        List<CustomCoupon> couponList = customCouponDao.findByCanUse(customId, totalMoney, proIds);
        return couponList;
    }

    /** 构建产品规格ID */
    private Integer [] buildProSpecsIds(List<ShoppingBasket> basketList) {
        Integer [] res = new Integer[basketList.size()];
        Integer index = 0;
        for(ShoppingBasket sb : basketList) {
            res[index++] = sb.getSpecsId();
        }
        return res;
    }

    /** 构建产品ID */
    private Integer [] buildProIds(List<ShoppingBasket> basketList) {
        List<Integer> list = new ArrayList<>();
        for(ShoppingBasket sb : basketList) {
            if(!list.contains(sb.getProId())) {list.add(sb.getProId());}
        }
        Integer [] res = new Integer[list.size()+1];
        res[0] = 0; //默认增加一个，否则当没有数据时会报错
        for(int i=0;i<list.size();i++) {res[i+1] = list.get(i);}
        return res;
    }

    /** 构建产品ID */
    private Integer [] buildProIdsByDto(List<ProductSpecsDto> dtoList) {
        List<Integer> list = new ArrayList<>();
        for(ProductSpecsDto psd : dtoList) {
            if(!list.contains(psd.getProId())) {list.add(psd.getProId());}
        }
        Integer [] res = new Integer[list.size()+1];
        res[0] = 0; //默认增加一个，否则当没有数据时会报错
        for(int i=0;i<list.size();i++) {res[i+1] = list.get(i);}
        return res;
    }

    private Integer [] genBasketIds(String ids) {
        if(ids==null) {return new Integer[0];}
        String [] array = ids.split("_");
        List<Integer> list = new ArrayList<>();
        for(String str:array) {
            try {
                Integer i = Integer.parseInt(str);
                list.add(i);
            } catch (Exception e) {
            }
        }
        return buildIds(list);
    }

    private Integer [] buildIds(List<Integer> list) {
        Integer [] res = new Integer[list.size()];
        Integer index = 0;
        for(Integer d : list) {
            res[index++] = d;
        }
        return res;
    }
}
