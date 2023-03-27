package com.increff.pos.helper;

import com.increff.pos.model.data.BrandReportData;
import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.SalesReportForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.DailyReportPojo;
import com.increff.pos.service.ApiException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsHelperUtil {
    public static BrandReportData convertBrandReport(List<Object> values, Integer id){
        BrandReportData d =new BrandReportData();
        d.setBrand((String) values.get(0));
        d.setCategory((String) values.get(1));
        d.setId(id);
        return d;
    }
    public static List<BrandReportData> covertBrandReport(Map<Integer, List<Object>> brandMap){
        List<BrandReportData> brandReportData = new ArrayList<>();
        brandMap.forEach((key, value) -> {
            brandReportData.add(convertBrandReport(value, key));
        });
        return brandReportData;
    }
    public static BrandPojo convertBrandReportForm(BrandForm form){
       BrandPojo brandPojo = new BrandPojo();
       brandPojo.setBrand(form.getBrand());
       brandPojo.setCategory(form.getCategory());
        return brandPojo;
    }
    public static List<BrandPojo> convertBrandReport(List<BrandReportData> brandReportDataList){
        List<BrandPojo> brandPojoList = new ArrayList<>();
        for(BrandReportData brandReportData: brandReportDataList){
            BrandPojo brandPojo = new BrandPojo();
            brandPojo.setBrand(brandReportData.getBrand());
            brandPojo.setCategory(brandReportData.getCategory());
            brandPojo.setId(brandReportData.getId());
            brandPojoList.add(brandPojo);
        }
        return brandPojoList;
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

    public static  Map<BrandPojo, Integer> convertInventoryReport(List<InventoryReportData> inventoryReportDataList){
        Map<BrandPojo, Integer> map = new HashMap<>();
        for(InventoryReportData inventoryReportData: inventoryReportDataList){
            BrandPojo brandPojo = new BrandPojo();
            brandPojo.setBrand(inventoryReportData.getBrand());
            brandPojo.setCategory(inventoryReportData.getCategory());
            map.put(brandPojo, inventoryReportData.getQuantity());
        }
        return map;
    }
    public static InventoryReportData convertInventoryReport(Integer quantity, List<Object> brandData){
        InventoryReportData inventoryReportData = new InventoryReportData();
        inventoryReportData.setQuantity(quantity);
        inventoryReportData.setBrand((String) brandData.get(0));
        inventoryReportData.setCategory((String) brandData.get(1));
        return inventoryReportData;
    }

    public static List<SalesReportData> convertSalesReport(Map<Integer, List<Object>> salesMap, Map<Integer, List<Object>> brandMap){
        List<SalesReportData> salesReportDataList = new ArrayList<>();
        salesMap.forEach((key, value) ->{
            SalesReportData salesReportData = new SalesReportData();
            salesReportData.setBrand((String) brandMap.get(key).get(0));
            salesReportData.setCategory((String) brandMap.get(key).get(1));
            salesReportData.setQuantity((Integer) value.get(0));
            salesReportData.setRevenue((Double) value.get(1));
            salesReportDataList.add(salesReportData);
        });
        return salesReportDataList;
    }
    public static Map<BrandPojo, List<Object>> convertSalesReport(List<SalesReportData> salesReportDataList){
        Map<BrandPojo, List<Object>> salesMap = new HashMap<>();
        for(SalesReportData salesReportData: salesReportDataList){
            List<Object> values = new ArrayList<>();
            BrandPojo brandPojo = new BrandPojo();
            brandPojo.setBrand(salesReportData.getBrand());
            brandPojo.setCategory(salesReportData.getCategory());
            values.add(salesReportData.getQuantity());
            values.add(salesReportData.getRevenue());
            salesMap.put(brandPojo, values);
        }
        return salesMap;
    }


    public static void validateSalesReport(SalesReportForm form) throws ApiException {
        if(form.getStartDate()==""){
            throw new ApiException("Start Date cannot be empty");
        }else if(form.getEndDate()==""){
            throw new ApiException("End Date cannot be empty");
        }else if(LocalDate.parse(form.getEndDate()).isBefore(LocalDate.parse(form.getStartDate()))){
            throw new ApiException("End Date cannot be before start date");
        }
    }




}
