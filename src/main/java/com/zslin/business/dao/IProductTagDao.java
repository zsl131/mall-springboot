package com.zslin.business.dao;

import com.zslin.business.model.ProductTag;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IProductTagDao extends BaseRepository<ProductTag, Integer>, JpaSpecificationExecutor<ProductTag> {

}
