package com.zslin.business.dao;

import com.zslin.business.model.AgentLevel;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IAgentLevelDao extends BaseRepository<AgentLevel, Integer>, JpaSpecificationExecutor<AgentLevel> {

}
