package com.zslin.business.app.service;

import com.zslin.business.dao.IProductFavoriteRecordDao;
import com.zslin.business.model.ProductFavoriteRecord;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.rabbit.RabbitUpdateTools;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiniProductFavoriteRecordService {

    @Autowired
    private IProductFavoriteRecordDao productFavoriteRecordDao;

    @Autowired
    private RabbitUpdateTools rabbitUpdateTools;

    public JsonResult addOrDelete(String params) {
        //System.out.println("----->"+params);
        WxCustomDto custom = JsonTools.getCustom(params);
        Integer proId = JsonTools.getParamInteger(params, "proId");
        String proTitle = JsonTools.getJsonParam(params, "proTitle");

        ProductFavoriteRecord pfr = productFavoriteRecordDao.findByProIdAndCustomId(proId, custom.getCustomId());
        if(pfr==null) {
            pfr = new ProductFavoriteRecord();
            pfr.setCreateDay(NormalTools.curDate());
            pfr.setCreateTime(NormalTools.curDatetime());
            pfr.setCreateLong(System.currentTimeMillis());
            pfr.setNickname(custom.getNickname());
            pfr.setOpenid(custom.getOpenid());
            pfr.setProId(proId);
            pfr.setProTitle(proTitle);
            pfr.setUnionid(custom.getUnionid());
            pfr.setCustomId(custom.getCustomId());
            productFavoriteRecordDao.save(pfr);
            plusCount(1, proId);
            return JsonResult.success("收藏成功").set("action", "save");
        } else {
            productFavoriteRecordDao.delete(pfr);
            plusCount(-1, proId);
            return JsonResult.success("取消收藏成功").set("action", "delete");
        }
    }

    private void plusCount(Integer amount, Integer proId) {
        rabbitUpdateTools.updateData("productDao", "plusFavoriteCount", amount, proId);
    }
}
