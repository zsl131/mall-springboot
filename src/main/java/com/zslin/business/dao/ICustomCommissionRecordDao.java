package com.zslin.business.dao;

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
}
