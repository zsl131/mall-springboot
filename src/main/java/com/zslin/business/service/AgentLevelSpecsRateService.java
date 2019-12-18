package com.zslin.business.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.business.dao.IAgentLevelSpecsRateDao;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.business.model.AgentLevelSpecsRate;
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

/**
 * Created by 钟述林 on 2019-12-18.
 */
@Service
@AdminAuth(name = "代理提成标准管理", psn = "销售管理", orderNum = 2, type = "1", url = "/admin/agentLevelSpecsRate")
@Explain(name = "代理提成标准管理", notes = "代理提成标准管理")
public class AgentLevelSpecsRateService {

    @Autowired
    private IAgentLevelSpecsRateDao agentLevelSpecsRateDao;

    @AdminAuth(name = "代理提成标准列表", orderNum = 1)
    @ExplainOperation(name = "代理提成标准列表", notes = "代理提成标准列表", params= {
             @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
             @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
             @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
             @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
     }, back = {
             @ExplainReturn(field = "size", type = "int", notes = "代理提成标准数量"),
             @ExplainReturn(field = "datas", type = "Object", notes = "代理提成标准数组对象")
     })
     public JsonResult list(String params) {
         QueryListDto qld = QueryTools.buildQueryListDto(params);
         Page<AgentLevelSpecsRate> res = agentLevelSpecsRateDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                 SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

         return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
     }

    @AdminAuth(name = "添加代理提成标准", orderNum = 2)
    @ExplainOperation(name = "添加代理提成标准", notes = "添加代理提成标准信息", params = {
            @ExplainParam(value = "id", name = "代理提成标准id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "添加成功的对象信息")
    })
    public JsonResult add(String params) {
        try {
            AgentLevelSpecsRate obj = JSONObject.toJavaObject(JSON.parseObject(params), AgentLevelSpecsRate.class);
            ValidationDto vd = ValidationTools.buildValidate(obj);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }
            agentLevelSpecsRateDao.save(obj);
            return JsonResult.succ(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @AdminAuth(name = "修改代理提成标准", orderNum = 3)
    @ExplainOperation(name = "修改代理提成标准", notes = "修改代理提成标准信息", params = {
            @ExplainParam(value = "id", name = "代理提成标准id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "对应的对象信息")
    })
    public JsonResult update(String params) {
        try {
            AgentLevelSpecsRate o = JSONObject.toJavaObject(JSON.parseObject(params), AgentLevelSpecsRate.class);
            ValidationDto vd = ValidationTools.buildValidate(o);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }
            AgentLevelSpecsRate obj = agentLevelSpecsRateDao.findOne(o.getId());
            MyBeanUtils.copyProperties(o, obj, "id", "createDate", "createTime", "createLong", "createDay");
            agentLevelSpecsRateDao.save(obj);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @AdminAuth(name = "获取代理提成标准", orderNum = 5)
    @ExplainOperation(name = "获取代理提成标准信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "代理提成标准ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            AgentLevelSpecsRate obj = agentLevelSpecsRateDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "删除代理提成标准", orderNum = 4)
    @ExplainOperation(name = "删除代理提成标准", notes = "通过ID删除对象", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
            @ExplainReturn(field = "flag", notes = "删除标识")
    })
    public JsonResult delete(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            AgentLevelSpecsRate r = agentLevelSpecsRateDao.findOne(id);
            agentLevelSpecsRateDao.delete(r);
            return JsonResult.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }


}