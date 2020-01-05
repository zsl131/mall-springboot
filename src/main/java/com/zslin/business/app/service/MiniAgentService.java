package com.zslin.business.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.dao.IAgentDao;
import com.zslin.business.dao.IAgentPaperDao;
import com.zslin.business.model.Agent;
import com.zslin.business.model.AgentPaper;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.exception.BusinessException;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiniAgentService {

    @Autowired
    private IAgentDao agentDao;

    @Autowired
    private IAgentPaperDao agentPaperDao;

    @NeedAuth(openid = true)
    @Transactional
    public JsonResult addAgent(String params) {
//        System.out.println(params);
        WxCustomDto dto = JsonTools.getCustom(params);
        Agent old = agentDao.findByUnionid(dto.getUnionid());
        if(old!=null) {
            throw new BusinessException(BusinessException.Code.HAS_EXISTS, "您已提交申请，请勿重复提交！");
        }
//        System.out.println(dto);
        Agent o = JSONObject.toJavaObject(JSON.parseObject(params), Agent.class);
        o.setOpenid(dto.getOpenid());
        o.setUnionid(dto.getUnionid());
        o.setNickname(dto.getNickname());
        agentDao.save(o);

        JSONArray jsonArray = JsonTools.str2JsonArray(JsonTools.getJsonParam(params, "papers")); //获取资质
        for(int i=0;i<jsonArray.size();i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            AgentPaper ap = new AgentPaper();
            ap.setAgentId(o.getId());
            ap.setAgentName(o.getName());
            ap.setFileName(jsonObj.getString("name"));
            ap.setFilePath(jsonObj.getString("url"));
            ap.setMediumId(jsonObj.getInteger("id"));
//            System.out.println("----->" + jsonObj.toJSONString());
            agentPaperDao.save(ap);
        }

        agentDao.updatePaperCount(jsonArray.size(), o.getId());
        return JsonResult.success("操作成功");
    }

    /**
     * 获取数据
     * @param params
     * @return
     */
    @NeedAuth(openid = true)
    public JsonResult loadOne(String params) {
        WxCustomDto dto = JsonTools.getCustom(params);
        Agent agent = agentDao.findByUnionid(dto.getUnionid());
        return JsonResult.success("获取成功").set("obj", agent);
    }
}
