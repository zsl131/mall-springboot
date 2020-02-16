package com.zslin.business.dao;

import com.zslin.business.model.Customer;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2019-12-01.
 */
public interface ICustomerDao extends BaseRepository<Customer, Integer>, JpaSpecificationExecutor<Customer> {

    Customer findByOpenid(String openid);

    @Query("UPDATE Customer c SET c.name=?1, c.phone=?2, c.agentId=?3 WHERE c.openid=?4")
    @Modifying
    @Transactional
    void updateName(String name, String phone, Integer agentId, String openid);
}
