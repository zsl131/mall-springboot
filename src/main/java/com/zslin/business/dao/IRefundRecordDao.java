package com.zslin.business.dao;

import com.zslin.business.model.RefundRecord;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2020-07-06.
 */
public interface IRefundRecordDao extends BaseRepository<RefundRecord, Integer>, JpaSpecificationExecutor<RefundRecord> {

}
