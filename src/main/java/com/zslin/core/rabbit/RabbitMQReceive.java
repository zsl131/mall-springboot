package com.zslin.core.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
}
