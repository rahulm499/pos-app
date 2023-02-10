package com.increff.pos.dto;

import com.increff.pos.controller.ReportsApiController;
import com.increff.pos.service.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReportScheduler {
    @Autowired
    ReportsApiController api;
    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyReportScheuler() throws ApiException {
        api.generateDailyReport();
    }
}
