package com.zslin.business.service;

import com.zslin.business.dao.IMediumDao;
import com.zslin.business.model.Medium;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.qiniu.tools.QiniuTools;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MediumService {

    @Autowired
    private IMediumDao mediumDao;

    @Autowired
    private QiniuTools qiniuTools;

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
}
