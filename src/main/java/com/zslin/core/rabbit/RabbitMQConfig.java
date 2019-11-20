package com.zslin.core.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String DIRECT_EXCHANGE = "MallDirectExchange";

    public static final String DIRECT_ROUTING = "MallDirectRouting";

    public static final String DIRECT_QUEUE = "MallDirectQueue";

    //队列 起名：TestDirectQueue
    @Bean
    public Queue testDirectQueue() {
        return new Queue(DIRECT_QUEUE, true);  //true 是否持久
    }

    //Direct交换机 起名：TestDirectExchange
    @Bean
    DirectExchange TestDirectExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }

    //绑定  将队列和交换机绑定, 并设置用于匹配键：TestDirectRouting
    @Bean
    Binding bindingDirect() {
        return BindingBuilder.bind(testDirectQueue()).to(TestDirectExchange()).with(DIRECT_ROUTING);
    }
}
