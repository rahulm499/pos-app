package com.increff.pos.scheduler;

import com.increff.pos.controller.ReportsController;
import com.increff.pos.dto.ReportsDto;
import com.increff.pos.service.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ReportScheduler {
    @Autowired
    private ReportsDto dto;
    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyReportScheuler() throws ApiException {
        dto.generateDailyReport();
    }
}
