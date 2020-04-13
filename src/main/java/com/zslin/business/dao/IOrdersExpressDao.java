package com.zslin.business.dao;

import com.zslin.business.model.OrdersExpress;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2020-04-12.
 */
public interface IOrdersExpressDao extends BaseRepository<OrdersExpress, Integer>, JpaSpecificationExecutor<OrdersExpress> {

    OrdersExpress findByOrdersNo(String ordersNo);

    OrdersExpress findByOrdersId(Integer ordersId);
}
