package com.zslin.business.dao;

import com.zslin.business.model.Coupon;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface ICouponDao extends BaseRepository<Coupon, Integer>, JpaSpecificationExecutor<Coupon> {

    @Query("SELECT c FROM Coupon c WHERE c.name LIKE %?1%")
    List<Coupon> searchByName(String name);
}
