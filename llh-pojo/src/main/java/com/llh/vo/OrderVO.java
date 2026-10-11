package com.llh.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.llh.entity.OrderDetail;
import com.llh.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO extends Orders implements Serializable {

    //订单商品信息
    @JsonProperty("orderDishes")
    private String orderProducts;

    //订单详情
    private List<OrderDetail> orderDetailList;

}
