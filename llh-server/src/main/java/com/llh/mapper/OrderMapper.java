package com.llh.mapper;

import com.github.pagehelper.Page;
import com.llh.dto.GoodsSalesDTO;
import com.llh.dto.OrdersPageQueryDTO;
import com.llh.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 往订单表插入一条数据
     */
    void insert(Orders orders);

    /**
     * 历史订单查询
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单详情
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 取消订单
     */
    void update(Orders orders);

    /**
     * 根据状态统计订单数量
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     *根据订单状态和订单时间来查询所有的订单
     */
    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime time);

    /**
     * 根据当天订单，统计已完成订单的金额总和
     */
    Double sumByMap(Map map);

    /**
     * 统计每日订单数
     */
    Integer countByMap(Map map);

    /**
     * 查询销量排名top10接口
     */
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);
}
