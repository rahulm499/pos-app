package com.increff.pos.flow;


import com.increff.pos.pojo.*;
import com.increff.pos.api.*;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.increff.pos.helper.ReportsHelperUtil.convertDailyReport;

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
    private DailyReportApi dailyReportApi;


    public List<BrandPojo> generateBrandReport(BrandPojo brandPojo){
        List<BrandPojo> brandPojos = new ArrayList<>();
        boolean isBrandEmpty = brandPojo.getBrand().equals("");
        boolean isCategoryEmpty = brandPojo.getCategory().equals("");
        if (!isBrandEmpty && !isCategoryEmpty) {
            BrandPojo brand = brandApi.getByBrandCategory(brandPojo.getBrand(), brandPojo.getCategory());
            if (brand != null) {
                brandPojos.add(brand);
            }
            return brandPojos;
        } else if (!isBrandEmpty) {
            return brandApi.getBrand(brandPojo.getBrand());
        } else if (!isCategoryEmpty) {
            return brandApi.getCategory(brandPojo.getCategory());
        }

        return brandApi.getAll();
    }


    public byte[] downloadBrandReport(List<BrandPojo> brandPojoList) {
        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);

        // Write data to CSV
        String[] header = {"Brand", "Category"};
        writer.writeNext(header);
        for (BrandPojo brandPojo : brandPojoList) {
            String[] data = {brandPojo.getBrand(), brandPojo.getCategory()};
            writer.writeNext(data);
        }
        return sw.toString().getBytes();
    }


    public Map<Integer, Integer> getBrandQuantityMap(List<BrandPojo> brandPojoList) throws ApiException {
        Map<Integer, Integer> map = new HashMap<>();
        for (BrandPojo brandPojo : brandPojoList) {
            int quantity = productApi.getBrandCategory(brandPojo.getId())
                    .stream()
                    .mapToInt(productPojo -> {
                        InventoryPojo inventoryPojo = inventoryApi.getByProduct(productPojo.getId());
                        return (inventoryPojo != null) ? inventoryPojo.getQuantity() : 0;
                    })
                    .sum();
            if (quantity > 0) {
                map.put(brandPojo.getId(), quantity);
            }
        }
        return map;
    }


    public byte[] downloadInventoryReport(List<BrandPojo> brandPojoList, Map<Integer, Integer> inventoryMap) {
        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);
        // Write data to CSV
        String[] header = {"Brand", "Category", "Quantity"};
        writer.writeNext(header);
        for (BrandPojo brandPojo : brandPojoList) {
            if (inventoryMap.containsKey(brandPojo.getId())) {
                String[] data = {brandPojo.getBrand(), brandPojo.getCategory(), String.valueOf(inventoryMap.get(brandPojo.getId()))};
                writer.writeNext(data);
            }
        }
        return sw.toString().getBytes();
    }


    public List<OrderPojo> getSalesReportOrderList(String sDate, String eDate) {
        ZonedDateTime startDate = LocalDate.parse(sDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime endDate = LocalDate.parse(eDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay(ZoneOffset.UTC).plusDays(1).minusSeconds(1);
        List<OrderPojo> orderPojoList = orderApi.getOrderByDate(startDate, endDate);
        return orderPojoList.stream()
                .filter(OrderPojo::getIsInvoiceGenerated)
                .collect(Collectors.toList());
    }

    public Map<Integer, Double> getSalesReportProductRevenueMap(List<BrandPojo> brandPojoList, List<OrderPojo> orderPojoList) throws ApiException {
        List<OrderItemPojo> productList =getProductList(orderPojoList);
        Map<Integer, Double> salesReportProducts = productList.stream()
                .collect(Collectors.groupingBy(OrderItemPojo::getProductId, Collectors.summingDouble(item -> item.getQuantity() * item.getSellingPrice())));
        return getSalesReportBrandProductMap(brandPojoList, salesReportProducts);
    }

    public Map<Integer, Double> getSalesReportProductQuantityMap(List<BrandPojo> brandPojoList, List<OrderPojo> orderPojoList) throws ApiException {
        List<OrderItemPojo> productList =getProductList(orderPojoList);
        Map<Integer, Double> salesReportProducts = productList.stream()
                .collect(Collectors.groupingBy(OrderItemPojo::getProductId, Collectors.summingDouble(OrderItemPojo::getQuantity)));
        return getSalesReportBrandProductMap(brandPojoList, salesReportProducts);
    }


    public byte[] downloadSalesReport(Map<Integer, Double> salesProductQuantityMap, Map<Integer, Double> salesProductRevenueMap, List<BrandPojo> brandPojoList) {
        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);
        // Write data to CSV
        String[] header = {"Brand", "Category", "Quantity", "Revenue"};
        writer.writeNext(header);
        for (BrandPojo brandPojo : brandPojoList) {
            if (salesProductQuantityMap.containsKey(brandPojo.getId())) {
                String[] data = {brandPojo.getBrand(), brandPojo.getCategory(), String.valueOf(salesProductQuantityMap.get(brandPojo.getId()).intValue()),
                        String.valueOf(salesProductRevenueMap.get(brandPojo.getId()))};
                writer.writeNext(data);
            }
        }
        return sw.toString().getBytes();
    }
    public void generateDailyReport() throws ApiException {
        List<OrderPojo> orderPojoList = generateDailyReportOrderList();
        int invoiced_items_count = 0;
        double revenue = 0;
        for (OrderPojo orderPojo : orderPojoList) {
            List<OrderItemPojo> orderItems = orderApi.getByOrderItemId(orderPojo.getId());
            for (OrderItemPojo orderItemPojo : orderItems) {
                invoiced_items_count += orderItemPojo.getQuantity();
                revenue += orderItemPojo.getSellingPrice() * orderItemPojo.getQuantity();
            }
        }
        DailyReportPojo dailyReportPojo = convertDailyReport(orderPojoList.size(), invoiced_items_count, revenue);
        dailyReportApi.add(dailyReportPojo);
    }



    public byte[] downloadDailyReport() {
        List<DailyReportPojo> dailyPojoList = dailyReportApi.getAll();
        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);

        // Write data to CSV
        String[] header = {"Date", "Invoiced Orders", "Invoiced Items", "Total Revenue"};
        writer.writeNext(header);
        for (DailyReportPojo dailyReportPojo : dailyPojoList) {
            String[] data = {String.valueOf(dailyReportPojo.getDate().withZoneSameInstant(ZoneId.of("UTC"))), String.valueOf(dailyReportPojo.getInvoiced_orders_count()), String.valueOf(dailyReportPojo.getInvoiced_items_count()), String.valueOf(dailyReportPojo.getTotal_revenue())};
            writer.writeNext(data);
        }
        return sw.toString().getBytes();
    }

    private List<OrderItemPojo> getProductList(List<OrderPojo> orderPojoList) throws ApiException {
        List<OrderItemPojo> productList = new ArrayList<>();
        for (OrderPojo orderPojo : orderPojoList) {
            List<OrderItemPojo> orderItems = orderApi.getByOrderItemId(orderPojo.getId());
            productList = Stream.concat(productList.stream(), orderItems.stream())
                    .collect(Collectors.toList());
        }
        return productList;
    }

    private Map<Integer, Double> getSalesReportBrandProductMap(List<BrandPojo> brandPojoList, Map<Integer, Double> salesReportProducts) throws ApiException {
        Map<Integer, Double> salesMap = new HashMap<>();
        for (BrandPojo brandPojo : brandPojoList) {
            productApi.getBrandCategory(brandPojo.getId())
                    .stream()
                    .map(ProductPojo::getId)
                    .filter(salesReportProducts::containsKey)
                    .map(salesReportProducts::get)
                    .collect(Collectors.groupingBy(
                            id -> brandPojo.getId(),
                            Collectors.summingDouble(Double::doubleValue)
                    ))
                    .forEach((brandId, sum) -> salesMap.put(brandId, sum));
        }
        return salesMap;
    }

    private List<OrderPojo> generateDailyReportOrderList() {
        ZonedDateTime startDate = LocalDate.now().atStartOfDay(ZoneOffset.UTC).minusDays(1);
        ZonedDateTime endDate = LocalDate.now().atStartOfDay(ZoneOffset.UTC).minusSeconds(1);
        List<OrderPojo> orderPojoList = orderApi.getOrderByDate(startDate, endDate);
        List<OrderPojo> newOrderPojoList = new ArrayList<>();
        for (OrderPojo orderPojo : orderPojoList) {
            if (orderPojo.getIsInvoiceGenerated()) {
                newOrderPojoList.add(orderPojo);
            }
        }
        return newOrderPojoList;
    }
}
