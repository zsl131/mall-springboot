package com.zslin.business.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.dao.IAgentApplyVerifyDao;
import com.zslin.business.dao.IAgentDao;
import com.zslin.business.dao.IAgentPaperDao;
import com.zslin.business.model.Agent;
import com.zslin.business.model.AgentApplyVerify;
import com.zslin.business.model.AgentPaper;
import com.zslin.business.tools.MediumTools;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.exception.BusinessException;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MiniAgentService {

    @Autowired
    private IAgentDao agentDao;

    @Autowired
    private IAgentPaperDao agentPaperDao;

    @Autowired
    private IAgentApplyVerifyDao agentApplyVerifyDao;

    @Autowired
    private MediumTools mediumTools;

    /**
     * 申请被驳回时重新提交
     * @param params
     * @return
     */
    public JsonResult updateAgent(String params) {
        //System.out.println("=======>"+params);
        WxCustomDto dto = JsonTools.getCustom(params);
        //System.out.println(dto);

        Integer id = JsonTools.getId(params);

        //System.out.println("+++++++++ID::: "+id);
        Agent agent = agentDao.findOne(id); //获取信息
        if(!"2".equals(agent.getStatus())) {throw new BusinessException(BusinessException.Code.STATUS_ERROR, "当前状态不可修改");}
        if(!dto.getUnionid().equals(agent.getUnionid())) {throw new BusinessException(BusinessException.Code.AUTH_ERROR, "无权限修改");}

        Agent o = JSONObject.toJavaObject(JSON.parseObject(params), Agent.class);

        agent.setName(o.getName());
        agent.setCityCode(o.getCityCode());
        agent.setCityName(o.getCityName());
        agent.setCountyCode(o.getCountyCode());
        agent.setCountyName(o.getCountyName());
        agent.setHasExperience(o.getHasExperience());
        agent.setIdentity(o.getIdentity());
        agent.setPhone(o.getPhone());
        agent.setProvinceCode(o.getProvinceCode());
        agent.setProvinceName(o.getProvinceName());
        agent.setSex(o.getSex());
        agent.setStreet(o.getStreet());
        agent.setAddressIndex(o.getAddressIndex());
        agent.setCustomId(dto.getCustomId());
        agent.setStatus("0"); //再次提交后状态需要修改为待审核
        agentDao.save(agent);

        JSONArray jsonArray = JsonTools.str2JsonArray(JsonTools.getJsonParam(params, "papers")); //获取资质
        for(int i=0;i<jsonArray.size();i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            AgentPaper ap = new AgentPaper();
            ap.setAgentId(o.getId());
            ap.setAgentName(o.getName());
            ap.setFileName(jsonObj.getString("name"));
            ap.setFilePath(jsonObj.getString("url"));
            ap.setMediumId(jsonObj.getInteger("id"));

            deleteOldPaper(ap.getAgentId(), ap.getFileName()); //删除原始数据
//            System.out.println("----->" + jsonObj.toJSONString());
            agentPaperDao.save(ap);
        }

        return JsonResult.success("提交成功，等待审核");
    }

    private void deleteOldPaper(Integer agentId, String fileName) {
        AgentPaper oldPaper = agentPaperDao.findByAgentIdAndFileName(agentId, fileName);
        if(oldPaper!=null) {
            mediumTools.deleteMedium(oldPaper.getMediumId()); //删除媒介信息
            agentPaperDao.delete(oldPaper);
        }
    }

    /**
     * 初次申请
     * @param params
     * @return
     */
    @NeedAuth(openid = true)
    @Transactional
    public JsonResult addAgent(String params) {
        //System.out.println("---->"+params);
        WxCustomDto dto = JsonTools.getCustom(params);
        //System.out.println(dto);

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
        JsonResult result = JsonResult.getInstance();
        if(agent!=null) {
            List<AgentApplyVerify> verifyList = agentApplyVerifyDao.findByUnionid(dto.getUnionid(), SimpleSortBuilder.generateSort("id_d"));
            List<AgentPaper> paperList = agentPaperDao.findByAgentId(agent.getId());
            result.set("verifyList", verifyList).set("paperList", paperList);
        }
        return result.set("obj", agent);
    }
}
