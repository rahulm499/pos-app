package com.increff.pos.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.flow.ReportsFlow;
import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.data.BrandReportData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.form.SalesReportForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import com.increff.pos.helper.ReportsHelperUtil;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

import static com.increff.pos.helper.ReportsHelperUtil.*;

@Service
public class ReportsDto {
    @Autowired
    private BrandApi brandApi;
    @Autowired
    private ProductApi productApi;
    @Autowired
    private InventoryApi inventoryApi;
    @Autowired
    private OrderApi orderApi;
    @Autowired
    private OrderItemApi orderItemApi;
    @Autowired
    private DailyReportApi dailyReportApi;
    @Autowired
    private ReportsFlow reportsFlow;
    public List<BrandReportData> generateBrandReport(BrandForm form) throws ApiException {
        return covertBrandReport(reportsFlow.generateBrandReport(convertBrandReportForm(form)));
    }

    public ResponseEntity<byte[]> downloadBrandReport(BrandForm form) throws IOException, ApiException {
        return reportsFlow.downloadBrandReport(reportsFlow.generateBrandReport(convertBrandReportForm(form)));
    }

    public List<InventoryReportData> generateInventoryReport(BrandForm form) throws ApiException {
        Map<Integer, List<Object>> brandMap = reportsFlow.generateBrandReport(convertBrandReportForm(form));
        Map<Integer, Integer> inventoryMap = reportsFlow.generateInventoryReport(brandMap);
        List<InventoryReportData> inventoryReportDataList = new ArrayList<>();
        inventoryMap.forEach((key, value) -> {
            InventoryReportData inventoryReportData = convertInventoryReport(value, brandMap.get(key));
            inventoryReportDataList.add(inventoryReportData);
        });
        return inventoryReportDataList;
    }

    public ResponseEntity<byte[]> downloadInventoryReport(BrandForm form) throws IOException, ApiException {
        Map<Integer, List<Object>> brandMap = reportsFlow.generateBrandReport(convertBrandReportForm(form));
        Map<Integer, Integer> inventoryMap = reportsFlow.generateInventoryReport(brandMap);
        return reportsFlow.downloadInventoryReport(brandMap, inventoryMap);
    }

    public List<SalesReportData> generateSalesReport(SalesReportForm form) throws ApiException {

        validateSalesReport(form);
        List<OrderPojo> orderPojoList = reportsFlow.getSalesReportOrderList(form.getStartDate(), form.getEndDate());
        Map<Integer, List<Object>> salesReportProductList= reportsFlow.getSalesReportProductList(orderPojoList);;
        Map<Integer, List<Object>> brandMap = reportsFlow.generateBrandReport(convertBrandReportForm(form));
        Map<Integer, List<Object>> salesMap = reportsFlow.getSalesReportData(brandMap, salesReportProductList);
        return convertSalesReport(salesMap, brandMap);
    }
    public ResponseEntity<byte[]> downloadSalesReport(SalesReportForm form) throws IOException, ApiException {

        validateSalesReport(form);
        List<OrderPojo> orderPojoList = reportsFlow.getSalesReportOrderList(form.getStartDate(), form.getEndDate());
        Map<Integer, List<Object>> salesReportProductList= reportsFlow.getSalesReportProductList(orderPojoList);;
        Map<Integer, List<Object>> brandMap = reportsFlow.generateBrandReport(convertBrandReportForm(form));
        Map<Integer, List<Object>> salesMap = reportsFlow.getSalesReportData(brandMap, salesReportProductList);
        return reportsFlow.downloadSalesReport(salesMap, brandMap);
    }
    public List<DailyReportData> getDailyReport(){
        List<DailyReportPojo> dailyPojoList = dailyReportApi.getAll();
        List<DailyReportData> data = new ArrayList<>();
        for(DailyReportPojo dailyReportPojo: dailyPojoList){
            data.add(convertDailyReport(dailyReportPojo));
        }
        return data;
    }

    public ResponseEntity<byte[]> downloadDailyReport() throws IOException {
        return reportsFlow.downloadDailyReport();
    }

    @Transactional(rollbackFor = ApiException.class)
    public void generateDailyReport() throws ApiException {
        List<OrderPojo> orderPojoList = reportsFlow.getDailyReportOrderList();
        int invoiced_items_count=0;
        double revenue=0;
        for(OrderPojo orderPojo: orderPojoList){
            List<OrderItemPojo> orderItems = orderItemApi.getByOrderId(orderPojo.getId());
            for(OrderItemPojo orderItemPojo: orderItems){
                invoiced_items_count+=orderItemPojo.getQuantity();
                revenue+=orderItemPojo.getSellingPrice();
            }
        }
        DailyReportPojo dailyReportPojo = convertDailyReport(orderPojoList.size(), invoiced_items_count, revenue);
        dailyReportApi.add(dailyReportPojo);
    }


}
