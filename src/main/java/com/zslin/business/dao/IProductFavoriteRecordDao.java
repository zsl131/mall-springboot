package com.zslin.business.dao;

import com.zslin.business.model.ProductFavoriteRecord;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2020-01-31.
 */
public interface IProductFavoriteRecordDao extends BaseRepository<ProductFavoriteRecord, Integer>, JpaSpecificationExecutor<ProductFavoriteRecord> {

    ProductFavoriteRecord findByProIdAndCustomId(Integer proId, Integer customId);
}
