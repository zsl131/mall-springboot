package com.zslin.business.mini.tools;

import com.zslin.business.dao.IOrdersDao;
import com.zslin.business.mini.dto.OrdersCountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 小程序订单统计工具
 */
@Component
public class MiniOrdersTools {

    @Autowired
    private IOrdersDao ordersDao;

    public List<OrdersCountDto> buildDto(Integer customId) {
        List<OrdersCountDto> result = new ArrayList<>();
        Integer count0 = ordersDao.queryCount("0", customId);
        result.add(new OrdersCountDto("0", "待付款", count0+""));

        Integer count1 = ordersDao.queryCount("1", customId);
        result.add(new OrdersCountDto("1", "待发货", count1+""));

        Integer count2 = ordersDao.queryCount("2", customId);
        result.add(new OrdersCountDto("2", "待收货", count2+""));

        Integer count3 = ordersDao.queryCount("3", customId);
        result.add(new OrdersCountDto("3", "待评价", count3+""));
        return result;
    }
}
