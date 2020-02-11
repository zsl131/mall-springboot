package com.zslin.business.app.service;

import com.zslin.business.dao.ICouponDao;
import com.zslin.business.dao.ICustomCouponDao;
import com.zslin.business.model.CustomCoupon;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.repository.SpecificationOperator;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class MiniCouponService {

    @Autowired
    private ICouponDao couponDao;

    @Autowired
    private ICustomCouponDao customCouponDao;

    public JsonResult listOwn(String params) {
        try {
            WxCustomDto custom = JsonTools.getCustom(params);
            String status =  JsonTools.getJsonParam(params, "status"); //0-全部；1-可使用；2-已过期；3-已使用

            QueryListDto qld = QueryTools.buildQueryListDto(params);
            Page<CustomCoupon> res = customCouponDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList(),
                    new SpecificationOperator("customId", "eq", custom.getCustomId()),
                    (status==null||"".equals(status) ||"0".equalsIgnoreCase(status))?null:
                            new SpecificationOperator("status", "eq", status)),
                    SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));
            return JsonResult.success().set("couponList", res.getContent());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error("出错", e.getMessage());
        }
    }
}
