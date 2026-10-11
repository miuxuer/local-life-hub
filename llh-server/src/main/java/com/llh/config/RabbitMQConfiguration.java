package com.llh.config;

import com.llh.constant.RabbitMQConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，声明订单超时取消用到的交换机与队列
 */
@Configuration
@Slf4j
public class RabbitMQConfiguration {

    /**
     * 订单延迟队列：消息在队列中存活15分钟，到期后作为死信转发到订单取消队列
     */
    @Bean
    public Queue orderDelayQueue() {
        log.info("开始生成订单延迟队列...");
        return QueueBuilder.durable(RabbitMQConstant.ORDER_DELAY_QUEUE)
                .ttl(RabbitMQConstant.ORDER_TIMEOUT)
                .deadLetterExchange(RabbitMQConstant.ORDER_DLX_EXCHANGE)
                .deadLetterRoutingKey(RabbitMQConstant.ORDER_CANCEL_ROUTING_KEY)
                .build();
    }

    @Bean
    public DirectExchange orderDelayExchange() {
        return new DirectExchange(RabbitMQConstant.ORDER_DELAY_EXCHANGE, true, false);
    }

    @Bean
    public Binding orderDelayBinding() {
        return BindingBuilder.bind(orderDelayQueue())
                .to(orderDelayExchange())
                .with(RabbitMQConstant.ORDER_DELAY_ROUTING_KEY);
    }

    /**
     * 订单取消队列：接收延迟队列投递过来的死信
     */
    @Bean
    public Queue orderCancelQueue() {
        return QueueBuilder.durable(RabbitMQConstant.ORDER_CANCEL_QUEUE).build();
    }

    @Bean
    public DirectExchange orderDlxExchange() {
        return new DirectExchange(RabbitMQConstant.ORDER_DLX_EXCHANGE, true, false);
    }

    @Bean
    public Binding orderCancelBinding() {
        return BindingBuilder.bind(orderCancelQueue())
                .to(orderDlxExchange())
                .with(RabbitMQConstant.ORDER_CANCEL_ROUTING_KEY);
    }

}
