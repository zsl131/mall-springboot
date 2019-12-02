package com.zslin.core.rabbit;

import com.zslin.business.dao.ICustomerDao;
import com.zslin.business.model.Customer;
import com.zslin.core.common.NormalTools;
import com.zslin.core.qiniu.tools.QiniuTools;
import com.zslin.core.tools.MyBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = RabbitMQConfig.DIRECT_QUEUE) //监听的队列名称 TestDirectQueue
@Slf4j
public class RabbitMQReceive {

    /*@Autowired
    private IUserDao userDao;*/

    @RabbitHandler
    public void process(Map testMessage) {
        String res = "DirectReceiver消费者收到消息  : " + testMessage.toString();
//        System.out.println(res);
        log.info(res);
    }

    /*@RabbitHandler
    public void addUser(User user) {
        userDao.save(user);
        log.info("添加用户信息： "+ user.toString());
    }*/

    @Autowired
    private ICustomerDao customerDao;
    @Autowired
    private QiniuTools qiniuTools;
    /** 处理小程序获取用户授权信息 */
    @RabbitHandler
    public void handlerCustomer(Customer customer) {
        Customer old = customerDao.findByOpenid(customer.getOpenid());
        String headimg = customer.getHeadImgUrl();
        if(customer.getHeadImgUrl()!=null && !"".equals(customer.getHeadImgUrl())) { //如果有头像
            headimg = qiniuTools.uploadCustomerHeadImg(headimg, customer.getOpenid()+".jpg");
        }
        customer.setHeadImgUrl(headimg);
        if(old==null) { //如果不存在
            customer.setFirstFollowDay(NormalTools.curDate());
            customer.setFirstFollowTime(NormalTools.curDatetime());
            customer.setFirstFollowLong(System.currentTimeMillis());
            customer.setFollowDay(NormalTools.curDate());
            customer.setFollowTime(NormalTools.curDatetime());
            customer.setFollowLong(System.currentTimeMillis());
            customerDao.save(customer);
        } else {
            MyBeanUtils.copyProperties(customer, old, "id", "firstFollowDay", "firstFollowTime", "firstFollowLong");
            customer.setFollowDay(NormalTools.curDate());
            customer.setFollowTime(NormalTools.curDatetime());
            customer.setFollowLong(System.currentTimeMillis());
            customerDao.save(old);
        }
    }
}
