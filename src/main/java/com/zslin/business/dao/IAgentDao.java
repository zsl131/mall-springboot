package com.zslin.business.dao;

import com.zslin.business.model.Agent;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2019-12-01.
 */
public interface IAgentDao extends BaseRepository<Agent, Integer>, JpaSpecificationExecutor<Agent> {

}
