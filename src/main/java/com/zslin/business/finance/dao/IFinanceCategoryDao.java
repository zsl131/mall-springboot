package com.zslin.business.finance.dao;

import com.zslin.business.finance.model.FinanceCategory;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by zsl on 2019/1/3.
 */
public interface IFinanceCategoryDao extends BaseRepository<FinanceCategory, Integer>, JpaSpecificationExecutor<FinanceCategory> {

}
