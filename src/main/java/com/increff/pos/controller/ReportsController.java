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
import java.io.IOException;
import java.util.List;

@Api
@RestController
public class ReportsController {
    @Autowired
    private ReportsDto dto;

    @ApiOperation(value= "Generates Brand Report")
    @RequestMapping(path = "/api/brand-report", method = RequestMethod.POST)
        public List<BrandReportData> generateBrandReport(@RequestBody BrandForm form) throws ApiException {
        return dto.generateBrandReport(form);
    }
    @ApiOperation(value= "Download Brand Report")
    @RequestMapping(path = "/api/brand-report/download", method = RequestMethod.POST)
    public ResponseEntity<byte[]> downloadBrandReport(@RequestBody BrandForm form) throws ApiException, IOException {
        return dto.downloadBrandReport(form);
    }

    @ApiOperation(value= "Gets Inventory Report")
    @RequestMapping(path = "/api/inventory-report", method = RequestMethod.POST)
    public List<InventoryReportData> generateInventoryReport(@RequestBody BrandForm form) throws ApiException {
        return dto.generateInventoryReport(form);
    }
    @ApiOperation(value= "Download Inventory Report")
    @RequestMapping(path = "/api/inventory-report/download", method = RequestMethod.POST)
    public ResponseEntity<byte[]> downloadInventoryReport(@RequestBody BrandForm form) throws ApiException, IOException {
        return dto.downloadInventoryReport(form);
    }

    @ApiOperation(value= "Gets Sales Report")
    @RequestMapping(path = "/api/sales-report", method = RequestMethod.POST)
    public List<SalesReportData> generateSalesReport(@RequestBody SalesReportForm form) throws ApiException {
        return dto.generateSalesReport(form);
    }
    @ApiOperation(value= "Download Sales Report")
    @RequestMapping(path = "/api/sales-report/download", method = RequestMethod.POST)
    public ResponseEntity<byte[]> downloadSalesReport(@RequestBody SalesReportForm form) throws ApiException, IOException {
        return dto.downloadSalesReport(form);
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
