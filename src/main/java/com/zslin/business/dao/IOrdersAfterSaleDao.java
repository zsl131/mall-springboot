package com.zslin.business.dao;

import com.zslin.business.model.OrdersAfterSale;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2019-12-01.
 */
public interface IOrdersAfterSaleDao extends BaseRepository<OrdersAfterSale, Integer>, JpaSpecificationExecutor<OrdersAfterSale> {

}
