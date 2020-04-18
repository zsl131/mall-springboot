package com.zslin.business.tools;

import com.zslin.business.dao.IAgentApplyVerifyDao;
import com.zslin.business.dao.IAgentLevelRecordDao;
import com.zslin.business.model.Agent;
import com.zslin.business.model.AgentApplyVerify;
import com.zslin.business.model.AgentLevel;
import com.zslin.business.model.AgentLevelRecord;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.LoginUserDto;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@HasTemplateMessage
public class AgentTools {

    @Autowired
    private IAgentApplyVerifyDao agentApplyVerifyDao;

    @Autowired
    private IAgentLevelRecordDao agentLevelRecordDao;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @TemplateMessageAnnotation(name = "申请审核通知", keys = "申请人-申请内容")
    public void verify(String params, Agent agent, AgentLevel al) {
        Integer id = JsonTools.getId(params);
        String status = JsonTools.getJsonParam(params, "status");
        String reason = JsonTools.getJsonParam(params, "reason");
        Integer level = JsonTools.getParamInteger(params, "level");

        LoginUserDto dto = JsonTools.getUser(params);

        AgentApplyVerify aav = new AgentApplyVerify();
        aav.setAgentId(id);
        if(al!=null) {
            reason += ("; 等级为："+level+"-"+al.getName());
        }
        aav.setContent(reason);
        aav.setOpenid(agent.getOpenid());
        aav.setUnionid(agent.getUnionid());
        aav.setVerifyDay(NormalTools.curDate());
        aav.setVerifyTime(NormalTools.curDatetime());
        aav.setVerifyLong(System.currentTimeMillis());
        aav.setVerifyOperator(dto.getId()+"-"+dto.getUsername()+"-"+dto.getNickname());
        aav.setVerifyRes(status);

        agentApplyVerifyDao.save(aav);

        /*String remark = "恭喜您！";
        if("1".equals(status) && al!=null) {remark = al.getName();}
        else if("2".equals(status)) {remark = reason;}
        rabbitNormalTools.pushMessage("AGENT-VERIFY", agent.getOpenid(), "pages/agent/apply/apply",
                "代理审核", "1".equals(status)?"审核通过":"审核不通过", NormalTools.curDate(),
                remark, agent.getName());*/

        addLevelRecord(agent, al, reason);

        String msgTitle = "很遗憾，审核不通过！";
        if("1".equals(status)) { //只有审核通过才进行等级调整
            msgTitle = "恭喜您，审核通过！";
        }

        sendTemplateMessageTools.send(agent.getOpenid(), "申请审核通知", "", msgTitle,
                TemplateMessageTools.field("申请人", agent.getName()),
                TemplateMessageTools.field("申请内容", "1".equals(status)?"通过":"驳回"),
                TemplateMessageTools.field("1".equals(status)?msgTitle+(al==null?"":"等级为："+al.getName()):"原因："+reason));
    }

    private void addLevelRecord(Agent agent, AgentLevel al, String reason) {
        if(al==null) {return;}
        AgentLevelRecord alr = new AgentLevelRecord();
        alr.setAgentId(agent.getId());
        alr.setBeforeLevelId(agent.getLevelId());
        alr.setBeforeLevelName(agent.getLevelName());
        alr.setCreateDay(NormalTools.curDate());
        alr.setCreateLong(System.currentTimeMillis());
        alr.setCreateTime(NormalTools.curDatetime());
        alr.setCurLevelId(al.getId());
        alr.setCurLevelName(al.getName());
//        alr.setFlag();
        alr.setReason(reason);
        agentLevelRecordDao.save(alr);
    }
}
