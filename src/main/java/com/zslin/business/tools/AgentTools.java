package com.zslin.business.tools;

import com.zslin.business.dao.IAgentApplyVerifyDao;
import com.zslin.business.dao.IAgentLevelRecordDao;
import com.zslin.business.model.Agent;
import com.zslin.business.model.AgentApplyVerify;
import com.zslin.business.model.AgentLevel;
import com.zslin.business.model.AgentLevelRecord;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.LoginUserDto;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AgentTools {

    @Autowired
    private IAgentApplyVerifyDao agentApplyVerifyDao;

    @Autowired
    private IAgentLevelRecordDao agentLevelRecordDao;

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
        addLevelRecord(agent, al, reason);
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
