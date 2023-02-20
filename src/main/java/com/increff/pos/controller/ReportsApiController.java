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
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Api
@RestController
public class ReportsApiController {
    @Autowired
    private ReportsDto dto;

    @ApiOperation(value= "Generates Brand Report")
    @RequestMapping(path = "/api/brand-report", method = RequestMethod.POST)
        public List<BrandReportData> generateBrandReport(@RequestBody BrandForm form) throws ApiException {
        return dto.generateBrandReport(form);
    }
    @ApiOperation(value= "Download Brand Report")
    @RequestMapping(path = "/api/brand-report/download", method = RequestMethod.POST)
    public ResponseEntity<byte[]> downloadBrandReport(HttpServletRequest request) throws ApiException, IOException {
        return dto.downloadBrandReport(IOUtils.toString(request.getReader()));
    }

    @ApiOperation(value= "Gets Inventory Report")
    @RequestMapping(path = "/api/inventory-report", method = RequestMethod.POST)
    public List<InventoryReportData> generateInventoryReport(@RequestBody BrandForm form) throws ApiException {
        return dto.generateInventoryReport(form);
    }
    @ApiOperation(value= "Download Inventory Report")
    @RequestMapping(path = "/api/inventory-report/download", method = RequestMethod.POST)
    public ResponseEntity<byte[]> downloadInventoryReport(HttpServletRequest request) throws ApiException, IOException {
        return dto.downloadInventoryReport(IOUtils.toString(request.getReader()));
    }

    @ApiOperation(value= "Gets Sales Report")
    @RequestMapping(path = "/api/sales-report", method = RequestMethod.POST)
    public List<SalesReportData> generateInventoryReport(@RequestBody SalesReportForm form) throws ApiException {
        return dto.getSalesReport(form);
    }
    @ApiOperation(value= "Download Sales Report")
    @RequestMapping(path = "/api/sales-report/download", method = RequestMethod.POST)
    public ResponseEntity<byte[]> downloadSalesReport(HttpServletRequest request) throws ApiException, IOException {
        return dto.downloadSalesReport(IOUtils.toString(request.getReader()));
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
    @ApiOperation(value= "Downloads daily Report")
    @RequestMapping(path = "/api/daily-report/download", method = RequestMethod.POST)
    public ResponseEntity<byte[]> downloadDailyReport() throws ApiException, IOException {
        return dto.downloadDailyReport();
    }
}
