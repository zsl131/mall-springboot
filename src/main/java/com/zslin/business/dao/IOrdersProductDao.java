package com.zslin.business.dao;

import com.zslin.business.model.OrdersProduct;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IOrdersProductDao extends BaseRepository<OrdersProduct, Integer>, JpaSpecificationExecutor<OrdersProduct> {

    List<OrdersProduct> findByOrdersId(Integer ordersId);
}
