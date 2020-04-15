package com.zslin.business.mini.service;

import com.zslin.business.dao.ICustomerDao;
import com.zslin.business.mini.dto.OrdersCountDto;
import com.zslin.business.mini.tools.MiniOrdersTools;
import com.zslin.business.model.Customer;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MiniCustomerService {

    @Autowired
    private ICustomerDao customerDao;

    @Autowired
    private MiniOrdersTools miniOrdersTools;

    /** 小程序-我 */
    public JsonResult me(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        Customer customer = customerDao.findByOpenid(customDto.getOpenid()); //获取客户
        List<OrdersCountDto> countList = miniOrdersTools.buildDto(customDto.getCustomId()); //生成订单数量DTO数据
        return JsonResult.success().set("customer", customer).set("countList", countList);
    }
}
