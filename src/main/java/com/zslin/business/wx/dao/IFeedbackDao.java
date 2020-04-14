package com.zslin.business.wx.dao;

import com.zslin.business.wx.model.Feedback;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2020-04-14.
 */
public interface IFeedbackDao extends BaseRepository<Feedback, Integer>, JpaSpecificationExecutor<Feedback> {

}
