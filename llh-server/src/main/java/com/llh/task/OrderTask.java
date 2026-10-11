package com.llh.task;

import com.llh.entity.Orders;
import com.llh.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    //处理一直处于派送中的订单
    @Scheduled(cron = "0 0 1 * * ?") //每天凌晨1点触发一次
    public void processDeliveryOrder() {
        log.info("处理一直处于派送中的订单:{}", LocalDateTime.now());

        //指定为当天订单
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);

        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }

        }

    }

}
