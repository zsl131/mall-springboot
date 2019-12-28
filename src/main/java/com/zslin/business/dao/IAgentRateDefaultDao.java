package com.zslin.business.dao;

import com.zslin.business.model.AgentRateDefault;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2019-12-28.
 */
public interface IAgentRateDefaultDao extends BaseRepository<AgentRateDefault, Integer>, JpaSpecificationExecutor<AgentRateDefault> {

    AgentRateDefault findByLevelId(Integer levelId);
}
