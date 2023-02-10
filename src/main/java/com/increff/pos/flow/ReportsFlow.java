package com.increff.pos.flow;

import com.increff.pos.model.data.BrandReportData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.SalesReportForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import com.increff.pos.util.helper.ReportsHelperUtil;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class ReportsFlow {

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
    @Transactional(rollbackOn = ApiException.class)
    public List<BrandPojo> getBrandReport(BrandForm form) throws ApiException {
        List<BrandPojo> brandPojos=new ArrayList<>();
        if(form.getBrand() != "" && form.getCategory()!=""){
            BrandPojo brand = brandService.getCheckBrandCategory(form.getBrand(), form.getCategory());
            if(brand != null){
                brandPojos.add(brand);
            }
            return brandPojos;
        }else if(form.getBrand() != ""){
            return brandService.getBrand(form.getBrand());
        }else if(form.getCategory()!=""){
            return brandService.getCategory(form.getCategory());
        }else{
            return brandService.getAll();
        }
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<InventoryReportData> getInventoryReport(List<BrandReportData> brandReportDataList) throws ApiException {
        List<InventoryReportData> inventoryReportDataList = new ArrayList<>();
        for(BrandReportData brandReportData: brandReportDataList){
            List<ProductPojo> productPojoList = productService.getBrandCategory(brandReportData.getId());
            int quantity=0;
            InventoryReportData inventoryReportData = new InventoryReportData();
            for(ProductPojo productPojo: productPojoList) {
                InventoryPojo inventoryPojo = inventoryService.getByProduct(productPojo.getId());
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

    protected InventoryReportData convertInventoryReport(Integer quantity, BrandReportData brandReportData){
        InventoryReportData inventoryReportData = new InventoryReportData();
        inventoryReportData.setQuantity(quantity);
        inventoryReportData.setBrand(brandReportData.getBrand());
        inventoryReportData.setCategory(brandReportData.getCategory());
        return inventoryReportData;
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<OrderPojo> getSalesReportOrderList(SalesReportForm form) throws ApiException {
        ZonedDateTime startDate = LocalDate.parse(form.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime endDate = LocalDate.parse(form.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay(ZoneOffset.UTC).plusDays(1).minusSeconds(1);
        return orderService.getOrderByDate(startDate, endDate);
    }

    @Transactional(rollbackOn = ApiException.class)
    public HashMap<Integer, Pair<Integer, Double>> getSalesReportProductList(List<OrderPojo> orderPojoList) throws ApiException {
        HashMap<Integer, Pair<Integer, Double>> map=new HashMap<Integer,Pair<Integer, Double>>();
        for(OrderPojo orderPojo: orderPojoList){
            List<OrderItemPojo> orderItems = orderItemService.getByOrderId(orderPojo.getId());
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

    @Transactional(rollbackOn = ApiException.class)
    public List<SalesReportData> getSalesReportData(List<BrandReportData> brandReportDataList,  HashMap<Integer, Pair<Integer, Double>> map) throws ApiException {
        List<SalesReportData> salesReportDataList = new ArrayList<>();
        for(BrandReportData brandReportData: brandReportDataList){
            List<ProductPojo> productPojoList = productService.getBrandCategory(brandReportData.getId());
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

    @Transactional(rollbackOn = ApiException.class)
    public List<OrderPojo> getDailyReportOrderList() throws ApiException {
        ZonedDateTime startDate = LocalDate.now().atStartOfDay(ZoneOffset.UTC).minusDays(1);
        ZonedDateTime endDate = LocalDate.now().atStartOfDay(ZoneOffset.UTC).minusSeconds(1);
        System.out.println("Report Generated at "+ LocalDate.now());
        return orderService.getOrderByDate(startDate, endDate);
    }
}
