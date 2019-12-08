package com.zslin.business.dao;

import com.zslin.business.model.Medium;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2019-12-08.
 */
public interface IMediumDao extends BaseRepository<Medium, Integer>, JpaSpecificationExecutor<Medium> {

}
