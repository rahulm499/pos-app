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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ReportsFlow {

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
    @Transactional(rollbackFor = ApiException.class)
    public List<BrandPojo> generateBrandReport(BrandForm form) throws ApiException {
        List<BrandPojo> brandPojos=new ArrayList<>();
        if(form.getBrand() != "" && form.getCategory()!=""){
            BrandPojo brand = brandApiService.getByBrandCategory(form.getBrand(), form.getCategory());
            if(brand != null){
                brandPojos.add(brand);
            }
            return brandPojos;
        }else if(form.getBrand() != ""){
            return brandApiService.getBrand(form.getBrand());
        }else if(form.getCategory()!=""){
            return brandApiService.getCategory(form.getCategory());
        }else{
            return brandApiService.getAll();
        }
    }

    @Transactional
    public ResponseEntity<byte[]> downloadBrandReport(List<BrandReportData> brandReportDataList) throws IOException {
        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);

        // Write data to CSV
        String[] header = {"Id", "Brand", "Category"};
        writer.writeNext(header);
        for (BrandReportData brandReportData : brandReportDataList) {
            String[] data = {String.valueOf(brandReportData.getId()), brandReportData.getBrand(), brandReportData.getCategory()};
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

    @Transactional(rollbackFor = ApiException.class)
    public List<InventoryReportData> generateInventoryReport(List<BrandReportData> brandReportDataList) throws ApiException {
        List<InventoryReportData> inventoryReportDataList = new ArrayList<>();
        for(BrandReportData brandReportData: brandReportDataList){
            List<ProductPojo> productPojoList = productApiService.getBrandCategory(brandReportData.getId());
            int quantity=0;
            InventoryReportData inventoryReportData = new InventoryReportData();
            for(ProductPojo productPojo: productPojoList) {
                InventoryPojo inventoryPojo = inventoryApiService.getByProduct(productPojo.getId());
                if(inventoryPojo != null){
                    quantity += inventoryPojo.getQuantity();
                    inventoryReportData = convertInventoryReport(quantity, brandReportData);
                }
            }
            if(inventoryReportData.getBrand()==null){
                continue;
            }
            inventoryReportDataList.add(inventoryReportData);
        }
        return inventoryReportDataList;
    }
    @Transactional
    public ResponseEntity<byte[]> downloadInventoryReport(List<InventoryReportData> inventoryReportDataList) throws IOException {
        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);

        // Write data to CSV
        String[] header = {"Brand", "Category", "Quantity"};
        writer.writeNext(header);
        for (InventoryReportData inventoryReportData : inventoryReportDataList) {
            String[] data = { inventoryReportData.getBrand(), inventoryReportData.getCategory(), String.valueOf(inventoryReportData.getQuantity())};
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
    protected InventoryReportData convertInventoryReport(Integer quantity, BrandReportData brandReportData){
        InventoryReportData inventoryReportData = new InventoryReportData();
        inventoryReportData.setQuantity(quantity);
        inventoryReportData.setBrand(brandReportData.getBrand());
        inventoryReportData.setCategory(brandReportData.getCategory());
        return inventoryReportData;
    }

    @Transactional(rollbackFor = ApiException.class)
    public List<OrderPojo> getSalesReportOrderList(SalesReportForm form) throws ApiException {
        ZonedDateTime startDate = LocalDate.parse(form.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime endDate = LocalDate.parse(form.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay(ZoneOffset.UTC).plusDays(1).minusSeconds(1);
        return orderApiService.getOrderByDate(startDate, endDate);
    }

    @Transactional(rollbackFor = ApiException.class)
    public HashMap<Integer, Pair<Integer, Double>> getSalesReportProductList(List<OrderPojo> orderPojoList) throws ApiException {
        HashMap<Integer, Pair<Integer, Double>> map=new HashMap<Integer,Pair<Integer, Double>>();
        for(OrderPojo orderPojo: orderPojoList){
            List<OrderItemPojo> orderItems = orderItemApiService.getByOrderId(orderPojo.getId());
            for(OrderItemPojo orderItemPojo: orderItems){
                int productId = orderItemPojo.getProductId();
                if(map.containsKey(productId)){
                    Integer newQuantity = orderItemPojo.getQuantity()+ map.get(productId).getKey();
                    Double newPrice = orderItemPojo.getSellingPrice()+ map.get(productId).getValue();
                    Pair<Integer, Double> P = new Pair<>(newQuantity, newPrice);
                    map.put(productId, P);
                }else{
                    Pair<Integer, Double> P = new Pair<>(orderItemPojo.getQuantity(), orderItemPojo.getSellingPrice());
                    map.put(productId, P);
                }
            }
        }
        return map;
    }

    @Transactional(rollbackFor = ApiException.class)
    public List<SalesReportData> getSalesReportData(List<BrandReportData> brandReportDataList,  HashMap<Integer, Pair<Integer, Double>> map) throws ApiException {
        List<SalesReportData> salesReportDataList = new ArrayList<>();
        for(BrandReportData brandReportData: brandReportDataList){
            List<ProductPojo> productPojoList = productApiService.getBrandCategory(brandReportData.getId());
            for(ProductPojo productPojo: productPojoList){
                SalesReportData tempData = new SalesReportData();
                int prodId = productPojo.getId();
                if(map.containsKey(prodId)){
                    tempData.setBrand(brandReportData.getBrand());
                    tempData.setCategory(brandReportData.getCategory());
                    tempData.setQuantity(map.get(prodId).getKey());
                    tempData.setRevenue(map.get(prodId).getValue());
                    salesReportDataList.add(tempData);
                }

            }
        }
        return salesReportDataList;
    }

    @Transactional
    public ResponseEntity<byte[]> downloadSalesReport(List<SalesReportData> salesReportDataList) throws IOException {
        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);

        // Write data to CSV
        String[] header = {"Brand", "Category", "Quantity", "Revenue"};
        writer.writeNext(header);
        for (SalesReportData salesReportData : salesReportDataList) {
            String[] data = { salesReportData.getBrand(), salesReportData.getCategory(), String.valueOf(salesReportData.getQuantity()),String.valueOf(salesReportData.getRevenue())};
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

    @Transactional(rollbackFor = ApiException.class)
    public List<OrderPojo> getDailyReportOrderList() throws ApiException {
        ZonedDateTime startDate = LocalDate.now().atStartOfDay(ZoneOffset.UTC).minusDays(1);
        ZonedDateTime endDate = LocalDate.now().atStartOfDay(ZoneOffset.UTC).minusSeconds(1);
        System.out.println("Report Generated at "+ LocalDate.now());
        List<OrderPojo> orderPojoList =  orderApiService.getOrderByDate(startDate, endDate);
        List<OrderPojo> newOrderPojoList = new ArrayList<>();
        for(OrderPojo orderPojo: orderPojoList){
            if(orderPojo.getIsInvoiceGenerated()){
                newOrderPojoList.add(orderPojo);
            }
        }
        return newOrderPojoList;
    }
    @Transactional
    public ResponseEntity<byte[]> downloadDailyReport(List<DailyReportData> dailyReportDataList) throws IOException {
        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);

        // Write data to CSV
        String[] header = {"Date", "Invoiced Orders", "Invoiced Items", "Total Revenue"};
        writer.writeNext(header);
        for (DailyReportData dailyReportData : dailyReportDataList) {
            String[] data = { dailyReportData.getDate(), String.valueOf(dailyReportData.getInvoiced_orders_count()),String.valueOf(dailyReportData.getInvoiced_items_count()),String.valueOf(dailyReportData.getTotal_revenue())};
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
