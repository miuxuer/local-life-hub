package com.llh.controller.admin;

import com.llh.result.Result;
import com.llh.service.ReportService;
import com.llh.vo.OrderReportVO;
import com.llh.vo.SalesTop10ReportVO;
import com.llh.vo.TurnoverReportVO;
import com.llh.vo.UserReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 数据统计相关接口
 */
@RestController
@RequestMapping("/admin/report")
@Slf4j
@Tag(name = "数据统计相关接口")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 营业额统计接口
     */
    @Operation(summary = "营业额统计接口")
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额数据统计:{},{}", begin, end);
        return Result.success(reportService.getTurnoverStatistics(begin, end));
    }

    /**
     * 用户统计接口
     */
    @Operation(summary = "用户统计接口")
    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户统计接口:{}, {}", begin, end);
        return Result.success(reportService.getUserStatistics(begin, end));
    }

    /**
     * 订单统计接口
     */
    @Operation(summary = "订单统计接口")
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("订单统计接口:{}, {}", begin, end);
        return Result.success(reportService.getOrdersStatistics(begin, end));
    }

    /**
     * 查询销量排名top10接口
     */
    @Operation(summary = "查询销量排名top10接口")
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("查询销量排名top10接口:{}, {}", begin, end);
        return Result.success(reportService.getSalesTop10(begin, end));
    }

}
