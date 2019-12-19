package com.zslin.core.dao;

import com.zslin.core.model.BaseTask;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IBaseTaskDao extends BaseRepository<BaseTask, Integer>, JpaSpecificationExecutor<BaseTask> {

}
