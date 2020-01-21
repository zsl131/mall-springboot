package com.zslin.business.app.service;

import com.zslin.business.dao.IAppModuleDao;
import com.zslin.business.dao.IAppNoticeDao;
import com.zslin.business.dao.ICarouselDao;
import com.zslin.business.dao.IProductDao;
import com.zslin.business.model.AppModule;
import com.zslin.business.model.AppNotice;
import com.zslin.business.model.Carousel;
import com.zslin.business.model.Product;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.repository.SpecificationOperator;
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

    /**
     * 小程序首页
     * @param params
     * @return
     */
    public JsonResult index(String params) {
        JsonResult result = JsonResult.getInstance();
        Sort sort = SimpleSortBuilder.generateSort("orderNo");
        List<Carousel> carouselList = carouselDao.findAll(QueryTools.getInstance().buildSearch(new SpecificationOperator("status", "eq", "1")), sort);
        List<AppModule> moduleList = appModuleDao.findAll(QueryTools.getInstance().buildSearch(new SpecificationOperator("status", "eq", "1")), sort);
        List<AppNotice> noticeList = appNoticeDao.findAll(QueryTools.getInstance().buildSearch(new SpecificationOperator("status", "eq", "1")));
        Page<Product> productList = productDao.findAll(QueryTools.getInstance().buildSearch(new SpecificationOperator("status", "eq", "1", "and"),
                new SpecificationOperator("saleMode", "eq", "1")),
                SimplePageBuilder.generate(0, 5, SimpleSortBuilder.generateSort("id_d")));
        result.set("carouseList", carouselList).set("moduleList", moduleList).
                set("noticeList", noticeList).set("productList", productList.getContent());
        return result;
    }
}
