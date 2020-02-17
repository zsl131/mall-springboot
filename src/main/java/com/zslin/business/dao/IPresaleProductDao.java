package com.zslin.business.dao;

import com.zslin.business.model.PresaleProduct;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2020-02-17.
 */
public interface IPresaleProductDao extends BaseRepository<PresaleProduct, Integer>, JpaSpecificationExecutor<PresaleProduct> {

    PresaleProduct findByProId(Integer proId);

    @Query("UPDATE PresaleProduct p SET p.status=?1 WHERE p.proId=?2")
    @Modifying
    @Transactional
    void updateStatus(String status, Integer proId);
}
