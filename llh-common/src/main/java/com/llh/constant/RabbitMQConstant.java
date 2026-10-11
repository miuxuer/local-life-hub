package com.llh.constant;

/**
 * RabbitMQ 常量类
 */
public class RabbitMQConstant {

    //订单延迟交换机
    public static final String ORDER_DELAY_EXCHANGE = "order.delay.exchange";
    //订单延迟队列
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    //订单延迟路由键
    public static final String ORDER_DELAY_ROUTING_KEY = "order.delay";
    //订单支付超时时间：15分钟
    public static final Integer ORDER_TIMEOUT = 15 * 60 * 1000;

    //订单死信交换机
    public static final String ORDER_DLX_EXCHANGE = "order.dlx.exchange";
    //订单取消队列
    public static final String ORDER_CANCEL_QUEUE = "order.cancel.queue";
    //订单取消路由键
    public static final String ORDER_CANCEL_ROUTING_KEY = "order.cancel";

}
