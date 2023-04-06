package com.increff.pos.helper;

import com.increff.pos.model.data.BrandReportData;
import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.SalesReportForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.DailyReportPojo;
import com.increff.pos.api.ApiException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

public class ReportsHelperUtil {
    public static BrandReportData convertBrandReport(BrandPojo brandPojo){
        BrandReportData d =new BrandReportData();
        d.setBrand(brandPojo.getBrand());
        d.setCategory(brandPojo.getCategory());
        d.setId(brandPojo.getId());
        return d;
    }
    public static List<BrandReportData> covertBrandReport(List<BrandPojo> brandPojoList){
        List<BrandReportData> brandReportData = new ArrayList<>();
        brandPojoList.forEach((brandPojo) -> {
            brandReportData.add(convertBrandReport(brandPojo));
        });
        return brandReportData;
    }
    public static BrandPojo convertBrandReportForm(BrandForm form){
       BrandPojo brandPojo = new BrandPojo();
       brandPojo.setBrand(form.getBrand());
       brandPojo.setCategory(form.getCategory());
        return brandPojo;
    }

    public static DailyReportData convertDailyReport(DailyReportPojo p){
        DailyReportData d = new DailyReportData();
        d.setDate(String.valueOf(p.getDate().withZoneSameInstant(ZoneId.of("UTC") )));
        d.setTotal_revenue(p.getTotal_revenue());
        d.setInvoiced_items_count(p.getInvoiced_items_count());
        d.setInvoiced_orders_count(p.getInvoiced_orders_count());
        return d;
    }
    public static DailyReportPojo convertDailyReport(Integer orderCount, Integer itemCount, Double revenue){
        DailyReportPojo dailyReportPojo = new DailyReportPojo();
        dailyReportPojo.setDate(LocalDate.now().atStartOfDay(ZoneOffset.UTC).minusDays(1));
        dailyReportPojo.setInvoiced_orders_count(orderCount);
        dailyReportPojo.setInvoiced_items_count(itemCount);
        dailyReportPojo.setTotal_revenue(revenue);
        return dailyReportPojo;
    }


    public static InventoryReportData convertInventoryReport(Integer quantity, BrandPojo brandPojo){
        InventoryReportData inventoryReportData = new InventoryReportData();
        inventoryReportData.setQuantity(quantity);
        inventoryReportData.setBrand(brandPojo.getBrand());
        inventoryReportData.setCategory(brandPojo.getCategory());
        return inventoryReportData;
    }

    public static List<SalesReportData> convertSalesReport( Map<Integer, Double> salesProductQuantityMap, Map<Integer, Double> salesProductRevenueMap,
                                                            List<BrandPojo> brandPojoList){
        List<SalesReportData> salesReportDataList = new ArrayList<>();
        for(BrandPojo brandPojo: brandPojoList){
            if(salesProductQuantityMap.containsKey(brandPojo.getId())) {
                SalesReportData salesReportData = new SalesReportData();
                salesReportData.setBrand((brandPojo.getBrand()));
                salesReportData.setCategory(brandPojo.getCategory());
                salesReportData.setQuantity(salesProductQuantityMap.get(brandPojo.getId()).intValue());
                salesReportData.setRevenue(salesProductRevenueMap.get(brandPojo.getId()));
                salesReportDataList.add(salesReportData);
            }
        }
        return salesReportDataList;
    }


    public static void validateSalesReport(SalesReportForm form) throws ApiException {
        if(Objects.equals(form.getStartDate(), "")){
            throw new ApiException("Start Date cannot be empty");
        }else if(Objects.equals(form.getEndDate(), "")){
            throw new ApiException("End Date cannot be empty");
        }else if(LocalDate.parse(form.getEndDate()).isBefore(LocalDate.parse(form.getStartDate()))){
            throw new ApiException("End Date cannot be before start date");
        }
    }


}
