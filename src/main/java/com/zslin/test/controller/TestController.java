package com.zslin.test.controller;

import com.zslin.core.common.NormalTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping(value = "test")
public class TestController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping(value = "index")
    public String index(String msg, HttpServletRequest request) {
        String res = msg + request.getRequestedSessionId()+ "  test in TestController => " + NormalTools.curDatetime();
        log.info(res);
        return res;
    }

    /*@GetMapping(value = "rabbit")
    public String rabbit(String msg, HttpServletRequest request) {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "test message, hello!";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map=new HashMap<>();
        map.put("messageId",messageId);
        map.put("messageData",messageData);
        map.put("msg", "send msg is : "+msg);
        map.put("sessionId", request.getSession().getId());
        map.put("requestId", request.getRequestedSessionId());
        map.put("createTime",createTime);
        //将消息携带绑定键值：TestDirectRouting 发送到交换机TestDirectExchange
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.DIRECT_ROUTING, map);
        return "ok";
    }*/

    /*@GetMapping(value = "addUser")
    public String addUser(String username) {
        User user = new User();
        user.setCreateTime(NormalTools.curDatetime());
        user.setIsAdmin("1");
        user.setNickname(username);
        user.setUsername(username);
        user.setPassword(username);
        user.setStatus("1");

//        userDao.save(user);
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.DIRECT_ROUTING, user);
        String res = " addUser => "+ user.toString();
        log.info(res);
        return res;
    }*/
}
