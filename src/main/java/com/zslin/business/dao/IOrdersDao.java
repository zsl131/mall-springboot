package com.zslin.business.dao;

import com.zslin.business.model.Orders;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IOrdersDao extends BaseRepository<Orders, Integer>, JpaSpecificationExecutor<Orders> {

    Orders findByOrdersNo(String ordersNo);

    Orders findByOrdersKey(String ordersKey);

    @Query("FROM Orders o WHERE o.id=?1 AND o.customId=?2")
    Orders findOne(Integer id, Integer customId);

    Orders findByOrdersNoAndCustomId(String ordersNo, Integer customId);

    /** 查询订单编号，用于支付 */
    @Query("SELECT o.ordersNo FROM Orders o WHERE o.ordersKey=?1 AND o.customId=?2")
    String queryOrdersNo(String ordersKey, Integer customId);

    @Query("UPDATE Orders o SET o.status=?1 WHERE o.ordersNo=?2 AND o.customId=?3")
    @Modifying
    @Transactional
    int updateStatus(String status, String ordersNo, Integer customId);
}
