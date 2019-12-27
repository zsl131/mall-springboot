package com.zslin.business.service;

import com.zslin.business.dao.IMediumDao;
import com.zslin.business.model.Medium;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.qiniu.tools.QiniuTools;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediumService {

    @Autowired
    private IMediumDao mediumDao;

    @Autowired
    private QiniuTools qiniuTools;

    @ExplainOperation(name = "删除对象的媒介信息", notes = "删除对象的媒介信息", params = {
            @ExplainParam(name = "id", value = "对象ID", type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "结果信息")
    })
    public JsonResult delete(String params) {
        try {
            Integer id = JsonTools.getId(params);
            Medium m = mediumDao.findOne(id);
            qiniuTools.deleteFile(m.getQiniuKey());
            mediumDao.delete(m);
            return JsonResult.success("删除成功");
        } catch (Exception e) {
            return JsonResult.error("删除失败");
        }
    }

    @ExplainOperation(name = "获取对象的媒介信息", notes = "获取对象的媒介信息", params = {
            @ExplainParam(name = "objType", value = "对象类型", type = "String", example = "product")
    }, back = {
            @ExplainReturn(field = "size", type = "int", notes = "数据数量"),
            @ExplainReturn(field = "data", type = "Object", notes = "数据列表")
    })
    public JsonResult listByObj(String params) {
        Integer id = JsonTools.getId(params);
        String objType = JsonTools.getJsonParam(params, "objType");
        Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
        List<Medium> list = mediumDao.findByObjClassNameAndObjId(objType, id, sort);
        return JsonResult.success().set("size", list.size()).set("data", list);
    }
}
