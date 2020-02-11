package com.zslin.business.dao;

import com.zslin.business.model.CustomCoupon;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface ICustomCouponDao extends BaseRepository<CustomCoupon, Integer>, JpaSpecificationExecutor<CustomCoupon> {

    CustomCoupon findByRuleSnAndReceiveKeyAndCustomId(String ruleSn, String receiveKey, Integer customId);

    /**
     * 获取用户优惠券
     * @param customId
     * @param hasRead
     * @return
     */
    List<CustomCoupon> findByCustomIdAndHasRead(Integer customId, String hasRead);
}
