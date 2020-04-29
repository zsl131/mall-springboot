package com.zslin.business.dao;

import com.zslin.business.mini.dto.AgentCommissionDto;
import com.zslin.business.model.CustomCommissionRecord;
import com.zslin.business.settlement.dto.RankingDto;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2020-02-20.
 */
public interface ICustomCommissionRecordDao extends BaseRepository<CustomCommissionRecord, Integer>, JpaSpecificationExecutor<CustomCommissionRecord> {

    List<CustomCommissionRecord> findByOrdersId(Integer ordersId);

    @Query("UPDATE CustomCommissionRecord c SET c.status=?1 WHERE c.ordersNo=?2")
    @Modifying
    @Transactional
    void updateStatus(String status, String ordersNo);

    //Integer agentId, String haveType, String status, Float money, Integer totalCount
    @Query("SELECT new com.zslin.business.mini.dto.AgentCommissionDto(c.agentId, c.haveType, c.status, SUM(c.money), COUNT(c.id)) FROM CustomCommissionRecord c WHERE c.status=?1 AND c.agentId=?2 ")
    AgentCommissionDto queryCountDto(String status, Integer agentId);

    @Query("UPDATE CustomCommissionRecord c SET c.cashOutBatchNo=?1, c.status=?2 WHERE c.status=?3 AND c.agentId=?4")
    @Modifying
    @Transactional
    void updateBatchNo(String batchNo, String newStatus, String oldStatus, Integer agentId);

    @Query("UPDATE CustomCommissionRecord c SET c.status=?1 WHERE c.cashOutBatchNo=?2 AND c.agentId=?3 ")
    @Modifying
    @Transactional
    void updateStatusByBatchNo(String status, String batchNo, Integer agentId);

    //获取排名信息
    //Integer agentId, String agentName, String agentPhone, Integer customId, String customNickname, Long specsCount, Double commissionMoney
    //haveType=0表示必须是自己推广的
    @Query("SELECT new com.zslin.business.settlement.dto.RankingDto" +
            "(c.agentId, a.name, a.phone, a.customId, a.nickname, a.openid, COUNT(c.id) as totalCount, SUM(c.money) as totalMoney) " +
            "FROM CustomCommissionRecord c, Agent a WHERE c.agentId=a.id AND c.haveType='0' AND c.createMonth=?1 AND c.status in ('1', '2', '3', '4', '5') " +
            " GROUP BY c.agentId ")
    Page<RankingDto> queryRanking(String createMonth, Pageable pageable);

    /** 获取用户指定月份是否有业绩，只要用户付款即表示有业绩，需要是自己推广的 */
    @Query("SELECT COUNT(c.id) FROM CustomCommissionRecord c WHERE c.createMonth=?1 AND c.haveType='0' AND c.agentOpenid=?2 AND " +
            "c.status in ('1', '2', '3', '4', '5')")
    Long queryCount(String createMonth, String agentOpenid);
}
