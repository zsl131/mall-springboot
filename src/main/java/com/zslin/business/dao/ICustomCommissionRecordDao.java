package com.zslin.business.dao;

import com.zslin.business.model.CustomCommissionRecord;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by 钟述林 on 2020-02-20.
 */
public interface ICustomCommissionRecordDao extends BaseRepository<CustomCommissionRecord, Integer>, JpaSpecificationExecutor<CustomCommissionRecord> {

    List<CustomCommissionRecord> findByOrdersId(Integer ordersId);
}
