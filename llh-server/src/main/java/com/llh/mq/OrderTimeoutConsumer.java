package com.llh.mq;

import com.llh.constant.RabbitMQConstant;
import com.llh.entity.Orders;
import com.llh.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 订单超时消息消费者
 */
@Component
@Slf4j
public class OrderTimeoutConsumer {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 消费延迟队列投递过来的死信，取消超过15分钟仍未支付的订单
     */
    @RabbitListener(queues = RabbitMQConstant.ORDER_CANCEL_QUEUE)
    public void processTimeoutOrder(String orderId) {
        log.info("收到订单超时消息:{}", orderId);

        Orders orders = orderMapper.getById(Long.valueOf(orderId));
        //订单不存在，或者已经被支付、被取消，则无需再处理
        if (orders == null || !Orders.PENDING_PAYMENT.equals(orders.getStatus())) {
            return;
        }

        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("订单超时，自动取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);

        log.info("订单超时，自动取消:{}", orderId);
    }

}
