package com.increff.pos.flow;

import com.increff.pos.model.data.BrandReportData;
import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.SalesReportForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import com.opencsv.CSVWriter;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportsFlow {

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
    @Transactional(rollbackFor = ApiException.class)
    public Map<Integer, List<Object>> generateBrandReport(BrandPojo brandPojo) throws ApiException {
        List<BrandPojo> brandPojos=new ArrayList<>();
        if(brandPojo.getBrand() != "" && brandPojo.getCategory()!=""){
            BrandPojo brand = brandApi.getByBrandCategory(brandPojo.getBrand(), brandPojo.getCategory());
            if(brand != null){
                brandPojos.add(brand);
            }
            return convertBrandPojoListToMap(brandPojos);
        }else if(brandPojo.getBrand() != ""){
            return convertBrandPojoListToMap(brandApi.getBrand(brandPojo.getBrand()));
        }else if(brandPojo.getCategory()!=""){
            return convertBrandPojoListToMap(brandApi.getCategory(brandPojo.getCategory()));
        }else{
            return convertBrandPojoListToMap(brandApi.getAll());
        }
    }

    public static Map<Integer, List<Object>> convertBrandPojoListToMap(List<BrandPojo> brandPojoList){
        Map<Integer, List<Object>> brandMap = new HashMap<>();
        for(BrandPojo brandPojo: brandPojoList){
            List<Object> values = new ArrayList<>();
            values.add(brandPojo.getBrand());
            values.add(brandPojo.getCategory());
            brandMap.put(brandPojo.getId(), values);
        }
        return brandMap;
    }

    @Transactional
    public ResponseEntity<byte[]> downloadBrandReport(Map<Integer, List<Object>> brandPojoList) throws IOException {
        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);

        // Write data to CSV
        String[] header = {"Brand", "Category"};
        writer.writeNext(header);
        brandPojoList.forEach((key, value) ->{
            String[] data = {(String) value.get(0), (String) value.get(1)};
            writer.writeNext(data);
        });
        byte[] csvData = sw.toString().getBytes();

        // create a ResponseEntity with the CSV data as its body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "data.csv");
        ResponseEntity<byte[]> response = new ResponseEntity<>(csvData, headers, HttpStatus.OK);
        return response;
    }

    @Transactional(rollbackFor = ApiException.class)
    public Map<Integer, Integer> generateInventoryReport(Map<Integer, List<Object>> brandPojoList) throws ApiException {
        Map<Integer, Integer> map = new HashMap<>();
        for (Map.Entry<Integer, List<Object>> entry : brandPojoList.entrySet()) {
            Integer key = entry.getKey();
            List<Object> value = entry.getValue();
            List<ProductPojo> productPojoList = productApi.getBrandCategory(key);
            int quantity = 0, flag = 0;
            for (ProductPojo productPojo : productPojoList) {
                InventoryPojo inventoryPojo = inventoryApi.getByProduct(productPojo.getId());
                if (inventoryPojo != null) {
                    quantity += inventoryPojo.getQuantity();
                    flag = 1;
                }
            }
            if (flag != 0) {
                map.put(key, quantity);
            }

        }
        return map;
    }
    @Transactional
    public ResponseEntity<byte[]> downloadInventoryReport( Map<Integer, List<Object>> brandMap, Map<Integer, Integer> inventoryMap) throws IOException {
        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);
        // Write data to CSV
        String[] header = {"Brand", "Category", "Quantity"};
        writer.writeNext(header);
        inventoryMap.forEach((key, value)->{
            String[] data = {(String) brandMap.get(key).get(0), (String) brandMap.get(key).get(1), String.valueOf(value)};
            writer.writeNext(data);
        });
        byte[] csvData = sw.toString().getBytes();

        // create a ResponseEntity with the CSV data as its body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "data.csv");
        ResponseEntity<byte[]> response = new ResponseEntity<>(csvData, headers, HttpStatus.OK);
        return response;
    }


    @Transactional(rollbackFor = ApiException.class)
    public List<OrderPojo> getSalesReportOrderList(String sDate, String eDate) throws ApiException {
        ZonedDateTime startDate = LocalDate.parse(sDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime endDate = LocalDate.parse(eDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay(ZoneOffset.UTC).plusDays(1).minusSeconds(1);
        List<OrderPojo> orderPojoList = orderApi.getOrderByDate(startDate, endDate);
        List<OrderPojo> newOrderPojoList = new ArrayList<>();
        for(OrderPojo orderPojo: orderPojoList){
            if(orderPojo.getIsInvoiceGenerated() == true){
                newOrderPojoList.add(orderPojo);
            }
        }
        return newOrderPojoList;
    }

    @Transactional(rollbackFor = ApiException.class)
    public Map<Integer, List<Object>> getSalesReportProductList(List<OrderPojo> orderPojoList) throws ApiException {
        Map<Integer, List<Object>> map=new HashMap<>();
        for(OrderPojo orderPojo: orderPojoList){
            List<OrderItemPojo> orderItems = orderItemApi.getByOrderId(orderPojo.getId());
            for(OrderItemPojo orderItemPojo: orderItems){
                int productId = orderItemPojo.getProductId();
                if(map.containsKey(productId)){
                    Integer newQuantity = orderItemPojo.getQuantity()+ (Integer) map.get(productId).get(0);
                    Double newRevenue = orderItemPojo.getQuantity()*orderItemPojo.getSellingPrice()+ (Double) map.get(productId).get(1);
                   List<Object> values = new ArrayList<>();
                   values.add(newQuantity);
                   values.add(newRevenue);
                    map.put(productId, values);
                }else{
                    List<Object> values = new ArrayList<>();
                    values.add(orderItemPojo.getQuantity());
                    values.add(orderItemPojo.getQuantity()*orderItemPojo.getSellingPrice());
                    map.put(productId, values);
                }
            }
        }
        return map;
    }

    @Transactional(rollbackFor = ApiException.class)
    public  Map<Integer, List<Object>> getSalesReportData(Map<Integer, List<Object>> brandMap, Map<Integer, List<Object>> salesReportProductList) throws ApiException {
        Map<Integer, List<Object>> salesMap = new HashMap<>();
        for (Map.Entry<Integer, List<Object>> entry : brandMap.entrySet()) {
            Integer key = entry.getKey();
            List<Object> value = entry.getValue();
            List<ProductPojo> productPojoList = productApi.getBrandCategory(key);
            int quantity = 0, flag = 0;
            double revenue = 0;
            for (ProductPojo productPojo : productPojoList) {
                int prodId = productPojo.getId();
                if (salesReportProductList.containsKey(prodId)) {
                    flag = 1;
                    quantity += (Integer) salesReportProductList.get(prodId).get(0);
                    revenue += (Double) salesReportProductList.get(prodId).get(1);
                }
            }
            if (flag == 1) {
                List<Object> values = new ArrayList<>();
                values.add(quantity);
                values.add(revenue);
                salesMap.put(key, values);
            }
        }
        return salesMap;
    }

    @Transactional
    public ResponseEntity<byte[]> downloadSalesReport(Map<Integer, List<Object>> salesMap, Map<Integer, List<Object>> brandMap) throws IOException {
        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);

        // Write data to CSV
        String[] header = {"Brand", "Category", "Quantity", "Revenue"};
        writer.writeNext(header);
        salesMap.forEach((key, value) -> {
            String[] data = {(String) brandMap.get(key).get(0), (String) brandMap.get(key).get(1), String.valueOf(value.get(0)),String.valueOf(value.get(1))};
            writer.writeNext(data);
        });
        byte[] csvData = sw.toString().getBytes();

        // create a ResponseEntity with the CSV data as its body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "data.csv");
        ResponseEntity<byte[]> response = new ResponseEntity<>(csvData, headers, HttpStatus.OK);
        return response;
    }

    @Transactional(rollbackFor = ApiException.class)
    public List<OrderPojo> getDailyReportOrderList() throws ApiException {
        ZonedDateTime startDate = LocalDate.now().atStartOfDay(ZoneOffset.UTC).minusDays(1);
        ZonedDateTime endDate = LocalDate.now().atStartOfDay(ZoneOffset.UTC).minusSeconds(1);
        System.out.println("Report Generated at "+ LocalDate.now());
        List<OrderPojo> orderPojoList =  orderApi.getOrderByDate(startDate, endDate);
        List<OrderPojo> newOrderPojoList = new ArrayList<>();
        for(OrderPojo orderPojo: orderPojoList){
            if(orderPojo.getIsInvoiceGenerated()){
                newOrderPojoList.add(orderPojo);
            }
        }
        return newOrderPojoList;
    }
    @Transactional
    public ResponseEntity<byte[]> downloadDailyReport() throws IOException {
        List<DailyReportPojo> dailyPojoList = dailyReportApi.getAll();
        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);

        // Write data to CSV
        String[] header = {"Date", "Invoiced Orders", "Invoiced Items", "Total Revenue"};
        writer.writeNext(header);
        for (DailyReportPojo dailyReportPojo : dailyPojoList) {
            String[] data = { String.valueOf(dailyReportPojo.getDate().withZoneSameInstant(ZoneId.of("UTC") )), String.valueOf(dailyReportPojo.getInvoiced_orders_count()),String.valueOf(dailyReportPojo.getInvoiced_items_count()),String.valueOf(dailyReportPojo.getTotal_revenue())};
            writer.writeNext(data);
        }
        byte[] csvData = sw.toString().getBytes();

        // create a ResponseEntity with the CSV data as its body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "data.csv");
        ResponseEntity<byte[]> response = new ResponseEntity<>(csvData, headers, HttpStatus.OK);
        return response;
    }
}
