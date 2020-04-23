package com.zslin.business.dao;

import com.zslin.business.model.Topic;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2020-04-22.
 */
public interface ITopicDao extends BaseRepository<Topic, Integer>, JpaSpecificationExecutor<Topic> {

    Topic findBySn(String sn);
}
