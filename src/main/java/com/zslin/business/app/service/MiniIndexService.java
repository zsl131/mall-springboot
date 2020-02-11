package com.zslin.business.app.service;

import com.zslin.business.dao.*;
import com.zslin.business.model.*;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.repository.SpecificationOperator;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MiniIndexService {

    @Autowired
    private ICarouselDao carouselDao;

    @Autowired
    private IAppModuleDao appModuleDao;

    @Autowired
    private IAppNoticeDao appNoticeDao;

    @Autowired
    private IProductDao productDao;

    @Autowired
    private ICustomCouponDao customCouponDao;

    @Autowired
    private ICouponDao couponDao;

    private static final String FIRST_FOLLOW = "FIRST_FOLLOW";

    /**
     * 小程序首页
     * @param params
     * @return
     */
    public JsonResult index(String params) {
        WxCustomDto custom = JsonTools.getCustom(params);
        JsonResult result = JsonResult.getInstance();
        Sort sort = SimpleSortBuilder.generateSort("orderNo");
        List<Carousel> carouselList = carouselDao.findAll(QueryTools.getInstance().buildSearch(new SpecificationOperator("status", "eq", "1")), sort);
        List<AppModule> moduleList = appModuleDao.findAll(QueryTools.getInstance().buildSearch(new SpecificationOperator("status", "eq", "1")), sort);
        List<AppNotice> noticeList = appNoticeDao.findAll(QueryTools.getInstance().buildSearch(new SpecificationOperator("status", "eq", "1")));
        Page<Product> productList = productDao.findAll(QueryTools.getInstance().buildSearch(new SpecificationOperator("status", "eq", "1", "and"),
                new SpecificationOperator("saleMode", "eq", "1")),
                SimplePageBuilder.generate(0, 5, SimpleSortBuilder.generateSort("id_d")));

        //判断用户是否已领取关注时赠送的优惠券
        CustomCoupon cc = customCouponDao.findByRuleSnAndReceiveKeyAndCustomId(FIRST_FOLLOW, custom.getCustomId()+"", custom.getCustomId());
        //如果未领取则提示领取
        if(cc==null) {
            result.set("needCoupon", true);
            result.set("coupon", couponDao.findByRuleSn(FIRST_FOLLOW)); //
        }

        result.set("carouseList", carouselList).set("moduleList", moduleList).
                set("noticeList", noticeList).set("productList", productList.getContent());
        return result;
    }
}
