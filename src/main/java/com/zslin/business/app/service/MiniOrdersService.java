package com.zslin.business.app.service;

import com.zslin.business.dao.ICustomAddressDao;
import com.zslin.business.dao.ICustomCouponDao;
import com.zslin.business.dao.IProductSpecsDao;
import com.zslin.business.dao.IShoppingBasketDao;
import com.zslin.business.model.CustomAddress;
import com.zslin.business.model.CustomCoupon;
import com.zslin.business.model.ProductSpecs;
import com.zslin.business.model.ShoppingBasket;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.tools.JsonTools;
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

    @NeedAuth(openid = true)
    public JsonResult onPay(String params) {
        JsonResult result = JsonResult.getInstance();
        try {
            String ids = JsonTools.getJsonParam(params, "ids");
            Integer addId = JsonTools.getParamInteger(params, "addressId"); //如果是指定收货地址
            WxCustomDto custom = JsonTools.getCustom(params);
            List<ShoppingBasket> basketList = shoppingBasketDao.findByIds(genBasketIds(ids));

            Integer [] proIds = buildProSpecsIds(basketList); //产品ID
            List<ProductSpecs> specsList = productSpecsDao.findByIds(proIds);
            CustomAddress address = null;
            if(addId!=null&&addId>0) {
                address = customAddressDao.findByCustomIdAndId(custom.getCustomId(), addId);
            }
            if(address==null) {
                address = customAddressDao.findDefaultAddress(custom.getCustomId()); //默认地址
            }
            Float totalMoney = buildTotalMoney(basketList);
            List<CustomCoupon> couponList = genCoupon(custom.getCustomId(), totalMoney, proIds); //满足条件的优惠券

            result.set("basketList", rebuildBasket(basketList, specsList)).set("specsList", specsList).set("address", address)
                .set("totalMoney", totalMoney).set("couponList", couponList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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

    /** 处理产品ID */
    private Integer [] buildProSpecsIds(List<ShoppingBasket> basketList) {
        Integer [] res = new Integer[basketList.size()];
        Integer index = 0;
        for(ShoppingBasket sb : basketList) {
            res[index++] = sb.getSpecsId();
        }
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
