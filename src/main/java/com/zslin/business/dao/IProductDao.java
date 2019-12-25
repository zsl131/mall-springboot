package com.zslin.business.dao;

import com.zslin.business.model.Product;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IProductDao extends BaseRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    /** 获取分类下的产品数量，用于删除分类前的判断 */
    @Query("SELECT COUNT(p.id) FROM Product p WHERE p.cateId=?1 ")
    Long findCountByCateId(Integer cateId);

    List<Product> findByCateId(Integer cateId);

    @Query("UPDATE Product p SET p.status=?1 WHERE p.id=?2 ")
    @Modifying
    @Transactional
    void updateStatus(String status, Integer id);

    @Query("UPDATE Product p SET p.specsCount=p.specsCount+?1 WHERE p.id=?2 ")
    @Modifying
    @Transactional
    void updateSpecsCount(Integer amount, Integer id);
}
