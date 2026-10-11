package com.llh.service.impl;

import com.llh.dto.GoodsSalesDTO;
import com.llh.entity.Orders;
import com.llh.mapper.OrderMapper;
import com.llh.mapper.UserMapper;
import com.llh.service.ReportService;
import com.llh.vo.OrderReportVO;
import com.llh.vo.SalesTop10ReportVO;
import com.llh.vo.TurnoverReportVO;
import com.llh.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 订单统计接口
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 用集合存放从begin到end所有天数的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //获取到当天的最开始的时间，精确到时分秒
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            //获取到当天的最后的时间，精确到时分秒
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //统计已完成的订单金额
            Map map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);

            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);

        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 用户统计接口
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //存放每天新增用户量
        List<Integer> newUserList = new ArrayList<>();
        //存放每天总用户量
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            //获取到当天的最开始的时间，精确到时分秒
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            //获取到当天的最后的时间，精确到时分秒
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map<String, LocalDateTime> map = new HashMap<>();

            //获取每天总用户数量
            map.put("end", endTime);
            Integer totalUser = userMapper.countByMap(map);
            totalUserList.add(totalUser);

            //获取每天新增用户数量
            map.put("begin", beginTime);
            Integer newUser = userMapper.countByMap(map);
            newUserList.add(newUser);

        }

        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 订单统计接口
     */
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //存放每日订单数
        List<Integer> totalOrderList = new ArrayList<>();
        //存放每日有效订单数
        List<Integer> validOrderList = new ArrayList<>();

        for (LocalDate date : dateList) {
            //获取到当天的最开始的时间，精确到时分秒
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            //获取到当天的最后的时间，精确到时分秒
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //每日订单数
            Integer totalCount = getOrderCount(beginTime, endTime, null);
            totalCount = totalCount == null ? 0 : totalCount;
            totalOrderList.add(totalCount);
            //每日有效订单数
            Integer validCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            validCount = validCount == null ? 0 : validCount;
            validOrderList.add(validCount);

        }
        //计算时间区间内订单总数量
        Integer totalOrderCount = totalOrderList.stream().reduce(Integer::sum).get();
        //计算时间区间内订单的有效数量
        Integer validOrderCount = validOrderList.stream().reduce(Integer::sum).get();
        //订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(totalOrderList, ","))
                .validOrderCountList(StringUtils.join(validOrderList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);

        return orderMapper.countByMap(map);
    }

    /**
     * 查询销量排名top10接口
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);

        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).toList();
        String nameList = StringUtils.join(names, ",");

        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).toList();
        String numberList = StringUtils.join(numbers, ",");

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }
}
