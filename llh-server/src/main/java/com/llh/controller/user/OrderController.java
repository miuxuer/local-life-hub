package com.llh.controller.user;

import com.llh.dto.OrdersPageQueryDTO;
import com.llh.dto.OrdersSubmitDTO;
import com.llh.result.PageResult;
import com.llh.result.Result;
import com.llh.service.OrderService;
import com.llh.vo.OrderSubmitVO;
import com.llh.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单相关接口
 */
@RestController("userOrderController")
@RequestMapping("/user/order")
@Tag(name = "订单相关接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     */
    @PostMapping("/submit")
    @Operation(summary = "用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单:{}", ordersSubmitDTO);

        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);

        return Result.success(orderSubmitVO);
    }

    /**
     * 历史订单查询
     */
    @Operation(summary = "历史订单查询")
    @GetMapping("/historyOrders")
    public Result<PageResult> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("历史订单查询:{}", ordersPageQueryDTO);

        PageResult pageResult = orderService.historyOrders(ordersPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 根据id查询订单详情
     */
    @Operation(summary = "根据id查询订单详情")
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> details(@PathVariable Long id){
        log.info("根据id查询订单详情:{}", id);

        OrderVO orderVO = orderService.details(id);

        return Result.success(orderVO);
    }

    /**
     * 取消订单
     */
    @Operation(summary = "取消订单")
    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Long id){
        log.info("取消订单:{},", id);

        orderService.userCancelById(id);

        return Result.success();
    }

    /**
     * 再来一单
     */
    @Operation(summary = "再来一单")
    @PostMapping("/repetition/{id}")
    public Result repetition(@PathVariable Long id){
        log.info("再来一单:{}", id);

        orderService.repetition(id);

        return Result.success();
    }


}
