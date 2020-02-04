package com.zslin.business.dao;

import com.zslin.business.model.ProductSpecs;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IProductSpecsDao extends BaseRepository<ProductSpecs, Integer>, JpaSpecificationExecutor<ProductSpecs> {

    /** 获取分类对应的规格，用于删除分类前的判断 */
    @Query("SELECT COUNT(s.id) FROM ProductSpecs s WHERE s.cateId=?1 ")
    Long findCountByCateId(Integer cateId);

    /** 获取产品对应的规格，用于删除产品前的判断 */
    @Query("SELECT COUNT(s.id) FROM ProductSpecs s WHERE s.proId=?1 ")
    Long findCountByProId(Integer proId);

    List<ProductSpecs> findByProId(Integer proId, Sort sort);

    @Query("SELECT MIN(s.price) FROM ProductSpecs s WHERE s.proId=?1")
    Float queryPrice(Integer proId);
}
