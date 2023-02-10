package com.increff.pos.controller;


import com.increff.pos.dto.ReportsDto;
import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.data.BrandReportData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.form.SalesReportForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api
@RestController
public class ReportsApiController {
    @Autowired
    private ReportsDto dto;

    @ApiOperation(value= "Gets Brand Report")
    @RequestMapping(path = "/api/brand-report", method = RequestMethod.POST)
        public List<BrandReportData> getBrandReport(@RequestBody BrandForm form) throws ApiException {
        return dto.getBrandReport(form);
    }

    @ApiOperation(value= "Gets Inventory Report")
    @RequestMapping(path = "/api/inventory-report", method = RequestMethod.POST)
    public List<InventoryReportData> getInventoryReport(@RequestBody BrandForm form) throws ApiException {
        return dto.getInventoryReport(form);
    }

    @ApiOperation(value= "Gets Sales Report")
    @RequestMapping(path = "/api/sales-report", method = RequestMethod.POST)
    public List<SalesReportData> getInventoryReport(@RequestBody SalesReportForm form) throws ApiException {
        return dto.getSalesReport(form);
    }


    @ApiOperation(value= "Generates daily Report")
    @RequestMapping(path = "/api/daily-report", method = RequestMethod.POST)
    public void generateDailyReport() throws ApiException {
        dto.generateDailyReport();
    }

    @ApiOperation(value= "Gets daily Report")
    @RequestMapping(path = "/api/daily-report", method = RequestMethod.GET)
    public List<DailyReportData> getDailyReport() throws ApiException {
        return dto.getDailyReport();
    }

}
