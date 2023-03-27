package com.increff.pos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.AbstractUnitTest;
import com.increff.pos.dao.*;
import com.increff.pos.dto.ReportsDto;
import com.increff.pos.model.data.BrandReportData;
import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.SalesReportForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.ApiException;
import jdk.nashorn.internal.objects.NativeJSON;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ReportsControllerTest extends AbstractUnitTest {
    @Autowired
    private ReportsController controller;
    @Autowired
    private ReportsDto reportsDto;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Before
    public void setup(){
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand("brand1");
        brandPojo.setCategory("cat1");
        brandDao.insert(brandPojo);

        ProductPojo productPojo = new ProductPojo();
        productPojo.setBrand_category(brandPojo.getId());
        productPojo.setBarcode("barcode");
        productPojo.setName("product");
        productPojo.setMrp(100.00);
        productDao.insert(productPojo);

        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(productPojo.getId());
        inventoryPojo.setQuantity(1000);
        inventoryDao.insert(inventoryPojo);

        productPojo = new ProductPojo();
        productPojo.setBrand_category(brandPojo.getId());
        productPojo.setBarcode("barcode2");
        productPojo.setName("product2");
        productPojo.setMrp(1000.00);
        productDao.insert(productPojo);

        inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(productPojo.getId());
        inventoryPojo.setQuantity(5000);
        inventoryDao.insert(inventoryPojo);

        OrderPojo orderPojo = new OrderPojo();
        orderPojo.setCreated_at(ZonedDateTime.now(ZoneId.of("UTC-4")));
        orderPojo.setIsInvoiceGenerated(true);
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        orderDao.insert(orderPojo);
        orderItemPojo.setOrderId(orderPojo.getId());
        orderItemPojo.setQuantity(100);
        orderItemPojo.setProductId(productPojo.getId());
        orderItemPojo.setSellingPrice(100.0);
        orderItemDao.insert(orderItemPojo);
    }

    @Test
    public void testGenerateBrandReport() throws ApiException {
        BrandForm brandForm = new BrandForm();
        brandForm.setBrand("");
        brandForm.setCategory("");
        List<BrandReportData> brandReportDataList = controller.generateBrandReport(brandForm);
        assertEquals(1, brandReportDataList.size());
        assertEquals("brand1", brandReportDataList.get(0).getBrand());
        assertEquals("cat1", brandReportDataList.get(0).getCategory());
    }
    @Test
    public void testDownloadBrandReport() throws ApiException, IOException {
        BrandForm brandForm = new BrandForm();
        brandForm.setBrand("");
        brandForm.setCategory("");
        ResponseEntity<byte[]> result = controller.downloadBrandReport(brandForm);
        assertNotEquals(0, result.toString().length());
    }

    @Test
    public void testGenerateInventoryReport() throws ApiException {
        BrandForm brandForm = new BrandForm();
        brandForm.setBrand("");
        brandForm.setCategory("");
        List<InventoryReportData> inventoryReportData = controller.generateInventoryReport(brandForm);
        assertEquals(1, inventoryReportData.size());
        assertEquals("brand1", inventoryReportData.get(0).getBrand());
        assertEquals("cat1", inventoryReportData.get(0).getCategory());
        assertEquals(Integer.valueOf(6000), inventoryReportData.get(0).getQuantity());
    }
    @Test
    public void testDownloadInventoryReport() throws ApiException, IOException {
        BrandForm brandForm = new BrandForm();
        brandForm.setBrand("");
        brandForm.setCategory("");
        ResponseEntity<byte[]> result = controller.downloadInventoryReport(brandForm);
        assertNotEquals(0, result.toString().length());
    }
    @Test
    public void testGenerateSalesReport() throws ApiException {
        SalesReportForm salesReportForm = new SalesReportForm();
        salesReportForm.setBrand("");
        salesReportForm.setCategory("");
        salesReportForm.setStartDate(String.valueOf(LocalDate.now()));
        salesReportForm.setEndDate(String.valueOf(LocalDate.now()));
        List<SalesReportData> salesReportData = controller.generateSalesReport(salesReportForm);
        assertEquals(1, salesReportData.size());
        assertEquals("brand1", salesReportData.get(0).getBrand());
        assertEquals("cat1", salesReportData.get(0).getCategory());
        assertEquals(Integer.valueOf(100), salesReportData.get(0).getQuantity());
        assertEquals(Double.valueOf(10000.0), salesReportData.get(0).getRevenue());
    }
    @Test
    public void testDownloadSalesReport() throws ApiException, IOException {
        SalesReportForm salesReportForm = new SalesReportForm();
        salesReportForm.setBrand("");
        salesReportForm.setCategory("");
        salesReportForm.setStartDate(String.valueOf(LocalDate.now()));
        salesReportForm.setEndDate(String.valueOf(LocalDate.now()));
        ResponseEntity<byte[]> result = controller.downloadSalesReport(salesReportForm);
        assertNotEquals(0, result.toString().length());
    }
    @Test
    public void testGetDailyReport() throws ApiException {
        reportsDto.generateDailyReport();
        List<DailyReportData> dailyReportData = controller.getDailyReport();
        assertEquals(1, dailyReportData.size());
    }
    @Test
    public void testDownloadDailyReport() throws ApiException, IOException {
        reportsDto.generateDailyReport();
        ResponseEntity<byte[]> result = controller.downloadDailyReport();
        assertNotEquals(0, result.toString().length());
    }
}
