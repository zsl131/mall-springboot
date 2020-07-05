package com.zslin.business.dao;

import com.zslin.business.model.OrdersProduct;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IOrdersProductDao extends BaseRepository<OrdersProduct, Integer>, JpaSpecificationExecutor<OrdersProduct> {

    List<OrdersProduct> findByOrdersId(Integer ordersId);

    List<OrdersProduct> findByOrdersNo(String ordersNo);

    @Query("UPDATE OrdersProduct o SET o.status=?1 WHERE o.ordersNo=?2 ")
    @Modifying
    @Transactional
    void updateStatus(String status, String ordersNo);

    @Query("UPDATE OrdersProduct o SET o.status=?1 WHERE o.id=?2 ")
    @Modifying
    @Transactional
    void updateStatusByProId(String status, Integer ordersProId);
}
