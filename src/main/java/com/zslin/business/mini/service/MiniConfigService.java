package com.zslin.business.mini.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.mini.dao.IMiniConfigDao;
import com.zslin.business.mini.model.MiniConfig;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.MyBeanUtils;
import com.zslin.core.tools.QueryTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Created by 钟述林 on 2019-12-01.
 */
@Service
@AdminAuth(name = "小程序配置管理", psn = "移动端管理", orderNum = 2, type = "1", url = "/admin/miniConfig")
@Explain(name = "小程序配置管理", notes = "小程序配置管理")
public class MiniConfigService {

    @Autowired
    private IMiniConfigDao miniConfigDao;

    @AdminAuth(name = "小程序配置列表", orderNum = 1)
    @ExplainOperation(name = "小程序配置列表", notes = "小程序配置列表", params= {
            @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
            @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
            @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
            @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
    }, back = {
            @ExplainReturn(field = "size", type = "int", notes = "小程序配置数量"),
            @ExplainReturn(field = "datas", type = "Object", notes = "小程序配置数组对象")
    })
    public JsonResult list(String params) {
        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<MiniConfig> res = miniConfigDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

        return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
    }

    @AdminAuth(name = "添加修改小程序配置", orderNum = 2)
    @ExplainOperation(name = "添加修改小程序配置", notes = "添加修改小程序配置信息", params = {
            @ExplainParam(value = "id", name = "小程序配置id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "保存成功的对象信息")
    })
    public JsonResult addOrUpdate(String params) {
        try {
            MiniConfig obj = JSONObject.toJavaObject(JSON.parseObject(params), MiniConfig.class);
            MiniConfig old = miniConfigDao.loadOne();
            if(old==null) {
                miniConfigDao.save(obj);
            } else {
                MyBeanUtils.copyProperties(obj, old);
                miniConfigDao.save(old);
            }
            return JsonResult.succ(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @ExplainOperation(name = "获取小程序配置信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "小程序配置ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    @NeedAuth(need = false)
    public JsonResult loadOne(String params) {
        try {
//            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
//            MiniConfig obj = miniConfigDao.findOne(id);
            MiniConfig obj = miniConfigDao.loadOne();
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "删除小程序配置", orderNum = 4)
    @ExplainOperation(name = "删除小程序配置", notes = "通过ID删除对象", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
            @ExplainReturn(field = "flag", notes = "删除标识")
    })
    public JsonResult delete(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            MiniConfig r = miniConfigDao.findOne(id);
            miniConfigDao.delete(r);
            return JsonResult.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }
}
