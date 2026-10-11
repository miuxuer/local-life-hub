package com.llh.mapper;

import com.llh.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 往订单明细表中插入数据
     */
    void insertBatch(List<OrderDetail> listOrderDetail);

    /**
     * 根据订单id查询订单明细表
     */
    @Select("select * from order_detail where order_id = #{ordersId}")
    List<OrderDetail> getByOrderId(Long ordersId);
}
