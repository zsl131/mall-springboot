package com.zslin.business.dao;

import com.zslin.business.model.AppModule;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2019-12-01.
 */
public interface IAppModuleDao extends BaseRepository<AppModule, Integer>, JpaSpecificationExecutor<AppModule> {

}
