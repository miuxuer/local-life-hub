package com.llh.service;

import com.llh.vo.OrderReportVO;
import com.llh.vo.SalesTop10ReportVO;
import com.llh.vo.TurnoverReportVO;
import com.llh.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);

    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
}
