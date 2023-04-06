package com.increff.pos.dto;

import com.increff.pos.flow.ReportsFlow;
import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.data.BrandReportData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.form.SalesReportForm;
import com.increff.pos.pojo.*;
import com.increff.pos.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.increff.pos.helper.ReportsHelperUtil.*;

@Service
public class ReportsDto {


    @Autowired
    private DailyReportApi dailyReportApi;
    @Autowired
    private OrderApi orderApi;
    @Autowired
    private ReportsFlow reportsFlow;

    public List<BrandReportData> generateBrandReport(BrandForm form) {
        return covertBrandReport(reportsFlow.generateBrandReport(convertBrandReportForm(form)));
    }

    public byte[] downloadBrandReport(BrandForm form) {
        return reportsFlow.downloadBrandReport(reportsFlow.generateBrandReport(convertBrandReportForm(form)));
    }

    public List<InventoryReportData> generateInventoryReport(BrandForm form) throws ApiException {
        List<BrandPojo> brandPojoList = reportsFlow.generateBrandReport(convertBrandReportForm(form));
        Map<Integer, Integer> brandQuantityMap = reportsFlow.getBrandQuantityMap(brandPojoList);
        List<InventoryReportData> inventoryReportDataList = new ArrayList<>();
        for (BrandPojo brandPojo : brandPojoList) {
            int brandId = brandPojo.getId();
            if (brandQuantityMap.containsKey(brandId)) {
                InventoryReportData inventoryReportData = convertInventoryReport(brandQuantityMap.get(brandId), brandPojo);
                inventoryReportDataList.add(inventoryReportData);
            }
        }
        return inventoryReportDataList;
    }

    public byte[] downloadInventoryReport(BrandForm form) throws ApiException {
        List<BrandPojo> brandPojoList = reportsFlow.generateBrandReport(convertBrandReportForm(form));
        Map<Integer, Integer> brandQuantityMap = reportsFlow.getBrandQuantityMap(brandPojoList);
        return reportsFlow.downloadInventoryReport(brandPojoList, brandQuantityMap);
    }

    public List<SalesReportData> generateSalesReport(SalesReportForm form) throws ApiException {

        validateSalesReport(form);
        List<OrderPojo> orderPojoList = reportsFlow.getSalesReportOrderList(form.getStartDate(), form.getEndDate());
        List<BrandPojo> brandPojoList = reportsFlow.generateBrandReport(convertBrandReportForm(form));
        Map<Integer, Double> salesProductQuantityMap = reportsFlow.getSalesReportProductQuantityMap(brandPojoList, orderPojoList);
        Map<Integer, Double> salesProductRevenueMap = reportsFlow.getSalesReportProductRevenueMap(brandPojoList, orderPojoList);
        return convertSalesReport(salesProductQuantityMap, salesProductRevenueMap, brandPojoList);
    }

    public byte[] downloadSalesReport(SalesReportForm form) throws ApiException {

        validateSalesReport(form);
        List<OrderPojo> orderPojoList = reportsFlow.getSalesReportOrderList(form.getStartDate(), form.getEndDate());
        List<BrandPojo> brandPojoList = reportsFlow.generateBrandReport(convertBrandReportForm(form));
        Map<Integer, Double> salesProductQuantityMap = reportsFlow.getSalesReportProductQuantityMap(brandPojoList, orderPojoList);
        Map<Integer, Double> salesProductRevenueMap = reportsFlow.getSalesReportProductRevenueMap(brandPojoList, orderPojoList);
        return reportsFlow.downloadSalesReport(salesProductQuantityMap, salesProductRevenueMap, brandPojoList);
    }

    public List<DailyReportData> getDailyReport() {
        List<DailyReportPojo> dailyPojoList = dailyReportApi.getAll();
        List<DailyReportData> dailyReportData = new ArrayList<>();
        for (DailyReportPojo dailyReportPojo : dailyPojoList) {
            dailyReportData.add(convertDailyReport(dailyReportPojo));
        }
        return dailyReportData;
    }

    public byte[] downloadDailyReport() {
        return reportsFlow.downloadDailyReport();
    }

    public void generateDailyReport() throws ApiException {
        reportsFlow.generateDailyReport();
    }


}
