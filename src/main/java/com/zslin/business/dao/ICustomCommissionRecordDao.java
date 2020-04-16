package com.zslin.business.dao;

import com.zslin.business.mini.dto.AgentCommissionDto;
import com.zslin.business.model.CustomCommissionRecord;
import com.zslin.core.repository.BaseRepository;
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

    //Integer agentId, String status, Float money, Integer totalCount
    @Query("SELECT new com.zslin.business.mini.dto.AgentCommissionDto(c.agentId, c.status, SUM(c.money), COUNT(c.id)) FROM CustomCommissionRecord c WHERE c.status=?1 AND c.agentId=?2 ")
    AgentCommissionDto queryCountDto(String status, Integer agentId);

    @Query("UPDATE CustomCommissionRecord c SET c.cashOutBatchNo=?1, c.status=?2 WHERE c.status=?3 AND c.agentId=?4")
    @Modifying
    @Transactional
    void updateBatchNo(String batchNo, String newStatus, String oldStatus, Integer agentId);

    @Query("UPDATE CustomCommissionRecord c SET c.status=?1 WHERE c.cashOutBatchNo=?2 AND c.agentId=?3 ")
    @Modifying
    @Transactional
    void updateStatusByBatchNo(String status, String batchNo, Integer agentId);
}
