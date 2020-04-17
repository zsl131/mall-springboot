package com.zslin.business.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.dao.IAgentApplyVerifyDao;
import com.zslin.business.dao.IAgentDao;
import com.zslin.business.dao.IAgentLevelDao;
import com.zslin.business.dao.IAgentPaperDao;
import com.zslin.business.model.Agent;
import com.zslin.business.model.AgentApplyVerify;
import com.zslin.business.model.AgentPaper;
import com.zslin.business.tools.MediumTools;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.business.wx.tools.WxAccountTools;
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
@HasTemplateMessage
public class MiniAgentService {

    @Autowired
    private IAgentDao agentDao;

    @Autowired
    private IAgentPaperDao agentPaperDao;

    @Autowired
    private IAgentLevelDao agentLevelDao;

    @Autowired
    private IAgentApplyVerifyDao agentApplyVerifyDao;

    @Autowired
    private MediumTools mediumTools;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    /**
     * 申请被驳回时重新提交
     * @param params
     * @return
     */
    @TemplateMessageAnnotation(name = "申请审核通知", keys = "申请人-申请内容")
    public JsonResult updateAgent(String params) {
        //System.out.println("=======>"+params);
        WxCustomDto dto = JsonTools.getCustom(params);
        //System.out.println(dto);

        Integer id = JsonTools.getId(params);

        //System.out.println("+++++++++ID::: "+id);
        Agent agent = agentDao.findOne(id); //获取信息
        if(!"2".equals(agent.getStatus())) {throw new BusinessException(BusinessException.Code.STATUS_ERROR, "当前状态不可修改");}
        if(!dto.getOpenid().equals(agent.getOpenid())) {throw new BusinessException(BusinessException.Code.AUTH_ERROR, "无权限修改");}

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
        if(o.getLeaderId()!=null && o.getLeaderId()>0) { //如果有上级代理
            agent.setLeaderId(o.getLeaderId());
            agent.setLeaderName(o.getLeaderName());
            agent.setLeaderPhone(o.getLeaderPhone());
            agent.setLeaderOpenid(o.getLeaderOpenid());
        }
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

        sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "申请审核通知", "", agent.getName()+"重新提交了代理申请",
                TemplateMessageTools.field("申请人", agent.getName()),
                TemplateMessageTools.field("申请内容", agent.getName()+"-"+agent.getPhone()),
                TemplateMessageTools.field("请及时登陆后台查看审核"));

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
    @TemplateMessageAnnotation(name = "申请审核通知", keys = "申请人-申请内容")
    public JsonResult addAgent(String params) {
        //System.out.println("---->"+params);
        WxCustomDto dto = JsonTools.getCustom(params);
        //System.out.println(dto);

        Agent old = agentDao.findByOpenid(dto.getOpenid());
        if(old!=null) {
            throw new BusinessException(BusinessException.Code.HAS_EXISTS, "您已提交申请，请勿重复提交！");
        }
//        System.out.println(dto);
        Agent o = JSONObject.toJavaObject(JSON.parseObject(params), Agent.class);
        o.setOpenid(dto.getOpenid());
        o.setUnionid(dto.getUnionid());
        o.setNickname(dto.getNickname());
        o.setCustomId(dto.getCustomId());
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

        sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "申请审核通知", "", o.getName()+"提交了代理申请",
                TemplateMessageTools.field("申请人", o.getName()),
                TemplateMessageTools.field("申请内容", o.getName()+"-"+o.getPhone()),
                TemplateMessageTools.field("请及时登陆后台查看审核"));

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
        Agent agent = agentDao.findByOpenid(dto.getOpenid());
        JsonResult result = JsonResult.getInstance();
        if(agent!=null) {
            List<AgentApplyVerify> verifyList = agentApplyVerifyDao.findByOpenid(dto.getOpenid(), SimpleSortBuilder.generateSort("id_d"));
            List<AgentPaper> paperList = agentPaperDao.findByAgentId(agent.getId());
            result.set("verifyList", verifyList).set("paperList", paperList);

            /*if("1".equals(agent.getStatus())) { //如果是正式代理
                List<AgentLevel> levelList = agentLevelDao.findAll();
                result.set("levelList", levelList);
            }*/
        }
        return result.set("obj", agent);
    }
}
