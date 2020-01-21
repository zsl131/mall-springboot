package com.zslin.business.dao;

import com.zslin.business.model.AgentLevelSpecsRate;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IAgentLevelSpecsRateDao extends BaseRepository<AgentLevelSpecsRate, Integer>, JpaSpecificationExecutor<AgentLevelSpecsRate> {

    @Query("FROM AgentLevelSpecsRate a WHERE a.proId=?1")
    List<AgentLevelSpecsRate> findByProduct(Integer proId);
}
