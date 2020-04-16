package com.zslin.business.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.dao.ICustomCommissionRecordDao;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.business.wx.tools.WxAccountTools;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.business.dao.ICashOutDao;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.business.model.CashOut;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import com.zslin.core.validate.ValidationDto;
import com.zslin.core.validate.ValidationTools;
import com.zslin.core.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import com.zslin.core.tools.MyBeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2020-04-17.
 */
@Service
@AdminAuth(name = "提现申请管理", psn = "结算管理", orderNum = 2, type = "1", url = "/admin/cashOut")
@Explain(name = "提现申请管理", notes = "提现申请管理")
@HasTemplateMessage
public class CashOutService {

    @Autowired
    private ICashOutDao cashOutDao;

    @Autowired
    private ICustomCommissionRecordDao customCommissionRecordDao;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @Transactional
    @TemplateMessageAnnotation(name = "返利到帐提醒", keys = "金额-时间")
    public JsonResult handleCash(String params) {
        try {
            Integer id = JsonTools.getId(params);
            CashOut cashOut = cashOutDao.findOne(id);
            cashOut.setStatus("1");
            cashOut.setPayLong(System.currentTimeMillis());
            cashOut.setPayTime(NormalTools.curDatetime());
            cashOut.setPayDate(NormalTools.curDate());

            customCommissionRecordDao.updateStatusByBatchNo("4", cashOut.getBatchNo(), cashOut.getAgentId());
            cashOutDao.save(cashOut);

            //快递公司-快递单号-商品信息-商品数量
            sendTemplateMessageTools.send(cashOut.getAgentOpenid(), "返利到帐提醒", "", "你的提现申请已处理啦",
                    TemplateMessageTools.field("金额", cashOut.getMoney() + " 元"),
                    TemplateMessageTools.field("时间", cashOut.getPayTime()),

                    TemplateMessageTools.field("请注意查收您的到款信息"));

            return JsonResult.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "提现申请列表", orderNum = 1)
    @ExplainOperation(name = "提现申请列表", notes = "提现申请列表", params= {
             @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
             @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
             @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
             @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
     }, back = {
             @ExplainReturn(field = "size", type = "int", notes = "提现申请数量"),
             @ExplainReturn(field = "datas", type = "Object", notes = "提现申请数组对象")
     })
     public JsonResult list(String params) {
         QueryListDto qld = QueryTools.buildQueryListDto(params);
         Page<CashOut> res = cashOutDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                 SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

         return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
     }

    @AdminAuth(name = "获取提现申请", orderNum = 5)
    @ExplainOperation(name = "获取提现申请信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "提现申请ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            CashOut obj = cashOutDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

}
