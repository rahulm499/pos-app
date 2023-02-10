package com.increff.pos.dto;

import com.increff.pos.flow.ReportsFlow;
import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.data.BrandReportData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.form.SalesReportForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import com.increff.pos.util.StringUtil;
import com.increff.pos.util.helper.ReportsHelperUtil;
import com.sun.org.apache.xpath.internal.operations.Or;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class ReportsDto {
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private DailyReportService dailyReportService;
    @Autowired
    private ReportsFlow reportsFlow;
    public List<BrandReportData> getBrandReport(BrandForm form) throws ApiException {
        return covertBrandReport(reportsFlow.getBrandReport(form));
    }

    public List<InventoryReportData> getInventoryReport(BrandForm form) throws ApiException {
        List<BrandReportData> brandReportDataList = getBrandReport(form);
        return reportsFlow.getInventoryReport(brandReportDataList);
    }

    public List<SalesReportData> getSalesReport(SalesReportForm form) throws ApiException {

        validateSalesReport(form);
        List<OrderPojo> orderPojoList = reportsFlow.getSalesReportOrderList(form);
        HashMap<Integer, Pair<Integer, Double>> map= reportsFlow.getSalesReportProductList(orderPojoList);;
        List<BrandReportData> brandReportDataList = getBrandReport(form);
        return reportsFlow.getSalesReportData(brandReportDataList, map);

    }
    public List<DailyReportData> getDailyReport(){
        List<DailyReportPojo> dailyPojoList = dailyReportService.getAll();
        List<DailyReportData> data = new ArrayList<>();
        for(DailyReportPojo dailyReportPojo: dailyPojoList){
            data.add(ReportsHelperUtil.convertDailyReport(dailyReportPojo));
        }
        return data;
    }


    public void generateDailyReport() throws ApiException {
        List<OrderPojo> orderPojoList = reportsFlow.getDailyReportOrderList();
        int invoiced_items_count=0;
        double revenue=0;
        for(OrderPojo orderPojo: orderPojoList){
            List<OrderItemPojo> orderItems = orderItemService.getByOrderId(orderPojo.getId());
            for(OrderItemPojo orderItemPojo: orderItems){
                invoiced_items_count+=orderItemPojo.getQuantity();
                revenue+=orderItemPojo.getSellingPrice();
            }
        }
        DailyReportPojo dailyReportPojo = convertDailyReport(orderPojoList.size(), invoiced_items_count, revenue);
        dailyReportService.add(dailyReportPojo);
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
