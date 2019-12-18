package com.zslin.business.dao;

import com.zslin.business.model.Medium;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2019-12-08.
 */
public interface IMediumDao extends BaseRepository<Medium, Integer>, JpaSpecificationExecutor<Medium> {

    /** 更新归属 */
    @Query("UPDATE Medium m SET m.objId=?1 WHERE m.objClassName=?2 AND m.ticket=?3 ")
    @Modifying
    @Transactional
    Integer modifyOwn(Integer objId, String objType, String ticket);

    Medium findByTicket(String ticket);
}