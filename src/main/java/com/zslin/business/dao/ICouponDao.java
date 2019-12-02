package com.zslin.business.dao;

import com.zslin.business.model.Coupon;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2019-12-01.
 */
public interface ICouponDao extends BaseRepository<Coupon, Integer>, JpaSpecificationExecutor<Coupon> {

}
