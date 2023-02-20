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
import com.increff.pos.util.helper.ReportsHelperUtil;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class ReportsDto {
    @Autowired
    private BrandApiService brandApiService;
    @Autowired
    private ProductApiService productApiService;
    @Autowired
    private InventoryApiService inventoryApiService;
    @Autowired
    private OrderApiService orderApiService;
    @Autowired
    private OrderItemApiService orderItemApiService;
    @Autowired
    private DailyReportApiService dailyReportApiService;
    @Autowired
    private ReportsFlow reportsFlow;
    public List<BrandReportData> generateBrandReport(BrandForm form) throws ApiException {
        return covertBrandReport(reportsFlow.generateBrandReport(form));
    }

    public ResponseEntity<byte[]> downloadBrandReport(String data) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<BrandReportData> brandReportDataList = objectMapper.readValue(data, new TypeReference<List<BrandReportData>>() {});
        return reportsFlow.downloadBrandReport(brandReportDataList);
    }

    public List<InventoryReportData> generateInventoryReport(BrandForm form) throws ApiException {
        List<BrandReportData> brandReportDataList = generateBrandReport(form);
        return reportsFlow.generateInventoryReport(brandReportDataList);
    }

    public ResponseEntity<byte[]> downloadInventoryReport(String data) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<InventoryReportData> inventoryReportDataList = objectMapper.readValue(data, new TypeReference<List<InventoryReportData>>() {});
        return reportsFlow.downloadInventoryReport(inventoryReportDataList);
    }

    public List<SalesReportData> getSalesReport(SalesReportForm form) throws ApiException {

        validateSalesReport(form);
        List<OrderPojo> orderPojoList = reportsFlow.getSalesReportOrderList(form);
        HashMap<Integer, Pair<Integer, Double>> map= reportsFlow.getSalesReportProductList(orderPojoList);;
        List<BrandReportData> brandReportDataList = generateBrandReport(form);
        return reportsFlow.getSalesReportData(brandReportDataList, map);

    }
    public ResponseEntity<byte[]> downloadSalesReport(String data) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<SalesReportData> salesReportDataList = objectMapper.readValue(data, new TypeReference<List<SalesReportData>>() {});
        return reportsFlow.downloadSalesReport(salesReportDataList);
    }
    public List<DailyReportData> getDailyReport(){
        List<DailyReportPojo> dailyPojoList = dailyReportApiService.getAll();
        List<DailyReportData> data = new ArrayList<>();
        for(DailyReportPojo dailyReportPojo: dailyPojoList){
            data.add(ReportsHelperUtil.convertDailyReport(dailyReportPojo));
        }
        return data;
    }

    public ResponseEntity<byte[]> downloadDailyReport() throws IOException {
        return reportsFlow.downloadDailyReport(getDailyReport());
    }

    @Transactional(rollbackFor = ApiException.class)
    public void generateDailyReport() throws ApiException {
        List<OrderPojo> orderPojoList = reportsFlow.getDailyReportOrderList();
        int invoiced_items_count=0;
        double revenue=0;
        for(OrderPojo orderPojo: orderPojoList){
            List<OrderItemPojo> orderItems = orderItemApiService.getByOrderId(orderPojo.getId());
            for(OrderItemPojo orderItemPojo: orderItems){
                invoiced_items_count+=orderItemPojo.getQuantity();
                revenue+=orderItemPojo.getSellingPrice();
            }
        }
        DailyReportPojo dailyReportPojo = convertDailyReport(orderPojoList.size(), invoiced_items_count, revenue);
        dailyReportApiService.add(dailyReportPojo);
    }

    protected void validateSalesReport(SalesReportForm form) throws ApiException {
        if(form.getStartDate()==""){
            throw new ApiException("Start Date cannot be empty");
        }else if(form.getEndDate()==""){
            throw new ApiException("End Date cannot be empty");
        }else if(LocalDate.parse(form.getEndDate()).isBefore(LocalDate.parse(form.getStartDate()))){
            throw new ApiException("End Date cannot be before start date");
        }
    }
    protected List<BrandReportData> covertBrandReport(List<BrandPojo> brandPojos){
        List<BrandReportData> brandReportData = new ArrayList<>();
        for(BrandPojo brandPojo: brandPojos){
            brandReportData.add(ReportsHelperUtil.convertBrandReport(brandPojo));
        }
        return brandReportData;
    }
    protected DailyReportPojo convertDailyReport(Integer orderCount, Integer itemCount, Double revenue){
        DailyReportPojo dailyReportPojo = new DailyReportPojo();
        dailyReportPojo.setDate(LocalDate.now().atStartOfDay(ZoneOffset.UTC).minusDays(1));
        dailyReportPojo.setInvoiced_orders_count(orderCount);
        dailyReportPojo.setInvoiced_items_count(itemCount);
        dailyReportPojo.setTotal_revenue(revenue);
        return dailyReportPojo;
    }
}
