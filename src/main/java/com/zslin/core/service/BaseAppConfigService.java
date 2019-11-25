package com.zslin.core.service;

import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dao.IAdminUserDao;
import com.zslin.core.dao.IBaseAppConfigDao;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.exception.BusinessException;
import com.zslin.core.model.AdminUser;
import com.zslin.core.model.BaseAppConfig;
import com.zslin.core.tools.InitSystemTools;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

/**
 * Created by zsl on 2018/7/8.
 */
@Service
@Explain(name = "系统配置管理", notes = "系统配置管理")
public class BaseAppConfigService {

    @Autowired
    private IBaseAppConfigDao baseAppConfigDao;

    @Autowired
    private InitSystemTools initSystemTools;

    @Autowired
    private IAdminUserDao userDao;

    @NeedAuth(need = false)
    @ExplainOperation(name = "获取系统配置", back = {
            @ExplainReturn(field = "size", type = "int", notes = "1：已经存在，0：不存在"),
            @ExplainReturn(field = "datas", type = "Object", notes = "配置对象")
    })
    public JsonResult loadOne(String params) {
        BaseAppConfig ac = baseAppConfigDao.loadOne();
        return JsonResult.getInstance().set("size", ac==null?0:1).set("datas", ac);
    }

    @NeedAuth(need = false)
    @ExplainOperation(name = "初始化系统", params = {
            @ExplainParam(value = "appName", name = "项目名称", require = true, example = "项目名称"),
            @ExplainParam(value = "nickname", name = "管理员昵称", require = true, example = "系统管理员"),
            @ExplainParam(value = "username", name = "管理员用户名", require = true, example = "admin"),
            @ExplainParam(value = "password", name = "管理员密码", require = true, example = "123456")
    }, back = {
            @ExplainReturn(field = "message", notes = "初始化结果信息")
    })
    public JsonResult initSystem(String params) {
        BaseAppConfig ac = baseAppConfigDao.loadOne();
        if(ac!=null && "1".equals(ac.getInitFlag())) {
//            return JsonResult.getInstance().fail("系统已经初始化，不可重复操作");
            return JsonResult.getInstance().failFlag("系统已经初始化，不可重复操作");
        }
        try {
            ac = new BaseAppConfig();
            ac.setAppName(JsonTools.getJsonParam(params, "appName"));
//            ac.setCreateDate(NormalTools.curDatetime());
            ac.setInitFlag("1");
            baseAppConfigDao.save(ac);

            AdminUser user = new AdminUser();
            user.setCreateDate(NormalTools.curDatetime());

            user.setNickname(JsonTools.getJsonParam(params, "nickname"));
            String username = JsonTools.getJsonParam(params, "username");
            String password = JsonTools.getJsonParam(params, "password");

            if(NormalTools.isNull(username)) {
                throw new BusinessException("用户名[username]不能为空");
            }

            user.setPassword(SecurityUtil.md5(username, password));
            user.setStatus("1");
            user.setIsAdmin("1");
            user.setUsername(username);
            userDao.save(user);

            initSystemTools.initSystem(user.getId());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }

        return JsonResult.getInstance().ok("系统初始化完成");
    }
}
