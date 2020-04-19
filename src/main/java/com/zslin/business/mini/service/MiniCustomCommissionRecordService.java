package com.zslin.business.mini.service;

import com.zslin.business.dao.IAgentDao;
import com.zslin.business.dao.ICashOutDao;
import com.zslin.business.dao.ICustomCommissionRecordDao;
import com.zslin.business.mini.dto.AgentCommissionDto;
import com.zslin.business.mini.tools.MiniOrdersTools;
import com.zslin.business.model.Agent;
import com.zslin.business.model.CashOut;
import com.zslin.business.model.CustomCommissionRecord;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.repository.SpecificationOperator;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import com.zslin.core.tools.RandomTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 小程序处理提成记录
 */
@Service
public class MiniCustomCommissionRecordService {

    @Autowired
    private ICustomCommissionRecordDao customCommissionRecordDao;

    @Autowired
    private MiniOrdersTools miniOrdersTools;

    @Autowired
    private IAgentDao agentDao;

    @Autowired
    private ICashOutDao cashOutDao;

    @NeedAuth(openid = true)
    public JsonResult listOwn(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        Integer agentId = agentDao.queryAgentId(customDto.getCustomId());
        List<AgentCommissionDto> dtoList = miniOrdersTools.buildAgentCommission(agentId);
        return JsonResult.success().set("commissionList", dtoList);
    }

    /** 获取明细 */
    @NeedAuth
    public JsonResult list(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        String status = JsonTools.getJsonParam(params, "status");

        Integer agentId = agentDao.queryAgentId(customDto.getCustomId()); //先获取代理ID

        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<CustomCommissionRecord> res = customCommissionRecordDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList(),
                new SpecificationOperator("agentId", "eq", agentId, "and"), //代理
                new SpecificationOperator("money", "gt", 0, "and"), //金额要大于0
                (status!=null&&!"".equals(status))?new SpecificationOperator("status", "eq", status, "and"):null), //对应状态
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

        return JsonResult.getInstance().set("size", (int) res.getTotalElements())
                .set("data", res.getContent());
    }

    /** 当用户发起提现 */
    @NeedAuth(openid = true)
    public JsonResult onCashOut(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        String batchNo = RandomTools.genTimeNo(3, 5).toUpperCase(); //批次号
        CashOut co = new CashOut();
        Agent agent = agentDao.findByOpenid(customDto.getOpenid());
        if(agent!=null && cashOutDao.findByRunningByAgentId(agent.getId())==null) { //不能有在提现中的数据

            AgentCommissionDto dto = customCommissionRecordDao.queryCountDto("2", agent.getId());

            co.setAgentId(agent.getId());
            co.setAgentName(agent.getName());
            co.setAgentOpenid(agent.getOpenid());
            co.setAgentPhone(agent.getPhone());
            co.setBatchNo(batchNo);
            co.setCreateDay(NormalTools.curDate());
            co.setCreateTime(NormalTools.curDatetime());
            co.setCreateLong(System.currentTimeMillis());
            co.setAmount((int)dto.getTotalCount());
            co.setMoney((float)dto.getMoney());
            co.setStatus("0");

            cashOutDao.save(co); //保存记录

            customCommissionRecordDao.updateBatchNo(batchNo, "3", "2", agent.getId());
            return JsonResult.success("提现申请成功，等待审核").set("flag", "1");
        } else {
            return JsonResult.success("提现失败，存在未完成的提现业务").set("flag", "0");
        }
    }
}
