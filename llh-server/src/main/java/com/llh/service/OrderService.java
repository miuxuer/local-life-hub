package com.llh.service;

import com.llh.dto.*;
import com.llh.result.PageResult;
import com.llh.vo.OrderStatisticsVO;
import com.llh.vo.OrderSubmitVO;
import com.llh.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderVO details(Long id);

    void userCancelById(Long id);

    void repetition(Long id);

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO statistics();

    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    void cancel(OrdersCancelDTO ordersCancelDTO);

    void delivery(Long id);

    void complete(Long id);
}
