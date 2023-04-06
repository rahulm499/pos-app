package com.increff.pos.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.pos.AbstractUnitTest;
import com.increff.pos.client.InvoiceClient;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.dto.OrderDto;
import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.InvoiceForm;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.api.ApiException;
import com.increff.pos.api.InvoiceClientApi;
import com.increff.pos.testUtilHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class OrderControllerTest extends AbstractUnitTest {
    @Autowired
    private OrderController controller;
    @Autowired
    private OrderFlow orderFlow;
    @Autowired
    private OrderDto orderDto;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    private InvoiceClientApi invoiceClientApi;

    @Before
    public void setup() throws JsonProcessingException {
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


    }

    @Test
    public void testGetAllOrder() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 1000, 100.0));
        OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
        controller.add(form);
        controller.add(form);
        List<OrderData> data = controller.getAll();
        assertEquals(2, data.size());

    }
    @Test //Happy Path
    public void testHappyPathAddOrder() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 1000, 100.0));
        OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);

        Integer inventory1 = inventoryDao.selectAll().get(0).getQuantity()-500;
        Integer inventory2 = inventoryDao.selectAll().get(1).getQuantity()-1000;
        controller.add(form);

        List<OrderData> data=controller.getAll();
        Collections.sort(data, (d1, d2) -> d1.getId() -d2.getId());
        assertEquals(false, data.get(0).getIsInvoiceGenerated());
        List<OrderItemData> orderItemDataList = data.get(0).getOrder();
        Collections.sort(orderItemDataList, (d1, d2) -> d1.getBarcode().length() -d2.getBarcode().length());
        assertEquals("barcode", orderItemDataList.get(0).getBarcode());
        assertEquals((Integer)500, orderItemDataList.get(0).getQuantity());
        assertEquals((Double)50.0, orderItemDataList.get(0).getSellingPrice());
        assertEquals("barcode2", orderItemDataList.get(1).getBarcode());
        assertEquals((Integer)1000, orderItemDataList.get(1).getQuantity());
        assertEquals((Double)100.0, orderItemDataList.get(1).getSellingPrice());
        assertEquals(inventory1, inventoryDao.selectAll().get(0).getQuantity());
        assertEquals(inventory2, inventoryDao.selectAll().get(1).getQuantity());
    }

    @Test
    public void testSadPathAddOrderEmpty(){
        // Empty order items
        try{List<OrderItemForm> orderItemFormList = new ArrayList<>();
            OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
            controller.add(form);}
        catch(ApiException e){assertEquals("Order does not contain any item", e.getMessage());}
    }

    @Test
    public void testSadPathAddOrderDuplicateItems(){

        // Duplicate Order items
        try{List<OrderItemForm> orderItemFormList = new ArrayList<>();
            orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
            orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode", 500, 100.0));
            OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
            controller.add(form);}
        catch(ApiException e){assertEquals("Found duplicate order item", e.getMessage());}
    }
    @Test
    public void testSadPathAddOrderQuantityEmpty(){

        // Quantity null
        try{List<OrderItemForm> orderItemFormList = new ArrayList<>();
            orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", (Integer)null , 50.0));
            OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
            controller.add(form);}
        catch(ApiException e){assertEquals("Quantity cannot be empty", e.getMessage());}

        // Quantity not positive
        try{List<OrderItemForm> orderItemFormList = new ArrayList<>();
            orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 0 , 50.0));
            OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
            controller.add(form);}
        catch(ApiException e){assertEquals("Quantity cannot be empty", e.getMessage());}
    }

    @Test
    public void testSadPathAddOrderSellingPrice(){
        // Selling Price Less than 0
        try{List<OrderItemForm> orderItemFormList = new ArrayList<>();
            orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 100 , -50.0));
            OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
            controller.add(form);}
        catch(ApiException e){assertEquals("Selling price cannot be negative", e.getMessage());}

        double price = productDao.select(inventoryDao.selectAll().get(0).getProductId()).getMrp();
        try{List<OrderItemForm> orderItemFormList = new ArrayList<>();
            orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", (Integer) 200, 5000.0));
            OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
            controller.add(form);}
        catch(ApiException e){assertEquals("Selling Price cannot be greater than MRP: "+ Double.toString(price), e.getMessage());}
    }

    @Test
    public void testSadPathAddOrderBarcodeNotExists(){
        // Barcode Check
        try{List<OrderItemForm> orderItemFormList = new ArrayList<>();
            orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode3", 100 , 50.0));
            OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
            controller.add(form);}
        catch(ApiException e){assertEquals("Product with given barcode does not exists, Barcode: " + "barcode3", e.getMessage());}

    }
    @Test
    public void testSadPathAddOrderQuantityGreater(){
        int quantity = inventoryDao.selectAll().get(0).getQuantity();
        try{List<OrderItemForm> orderItemFormList = new ArrayList<>();
            orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", (Integer) 2000, 50.0));
            OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
            controller.add(form);}
        catch(ApiException e){assertEquals("Quantity cannot be greater than "+ quantity, e.getMessage());}
    }

    @Test
    public void testHappyPathGetOrder() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 1000, 100.0));
        OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
        controller.add(form);
        OrderData data = controller.getAll().get(0);
        OrderData orderData = controller.get(data.getId());
        assertEquals(false, orderData.getIsInvoiceGenerated());
        assertEquals(2, orderData.getOrder().size());
        List<OrderItemData> orderItemDataList = orderData.getOrder();
    }

    @Test
    public void testSadPathGetOrder() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 1000, 100.0));
        OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
        controller.add(form);
        OrderData data = controller.getAll().get(0);
        int id =data.getId()+1;
        try{OrderData orderData = controller.get(id);}
        catch (ApiException e){assertEquals("Order with given ID does not exists, id: " + id,e.getMessage());}
    }

    @Test
    public void testHappyPathUpdateOrder() throws ApiException {
        Integer inventory1 = inventoryDao.selectAll().get(0).getQuantity();
        Integer inventory2 = inventoryDao.selectAll().get(1).getQuantity();
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 1000, 100.0));
        OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);

        controller.add(form);
        OrderData data = controller.getAll().get(0);
        orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 10, 10.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 10, 1.0));
        form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
        controller.update(form, data.getId());

        inventory1=inventory1-10;
        inventory2=inventory2-10;

        OrderData orderData = controller.get(data.getId());
        assertEquals(false, orderData.getIsInvoiceGenerated());
        assertEquals(2, orderData.getOrder().size());
        List<OrderItemData> orderItemDataList = orderData.getOrder();
        Collections.sort(orderItemDataList, (d1, d2) -> d1.getBarcode().length() -d2.getBarcode().length());
        assertEquals("barcode", orderItemDataList.get(0).getBarcode());
        assertEquals((Integer)10, orderItemDataList.get(0).getQuantity());
        assertEquals((Double)10.0, orderItemDataList.get(0).getSellingPrice());
        assertEquals("barcode2", orderItemDataList.get(1).getBarcode());
        assertEquals((Integer)10, orderItemDataList.get(1).getQuantity());
        assertEquals((Double)1.0, orderItemDataList.get(1).getSellingPrice());

        assertEquals(inventory1, inventoryDao.selectAll().get(0).getQuantity());
        assertEquals(inventory2, inventoryDao.selectAll().get(1).getQuantity());


    }

    @Test
    public void testSadPathUpdateOrderItemNotExist() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 1000, 100.0));
        OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
        controller.add(form);

        OrderData data = controller.getAll().get(0);
        int inventory1 = data.getOrder().get(0).getQuantity();

        int id = data.getId()+1;
        try{orderItemFormList = new ArrayList<>();
            orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 10, 10.0));
            orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 10, 1.0));
            controller.update(testUtilHelper.getDummyOrderFrom(orderItemFormList), id);}
        catch(ApiException e){assertEquals("Order with given ID does not exists, id: " + id, e.getMessage());}
    }
    @Test
    public void testSadPathUpdateOrderEmpty() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 1000, 100.0));
        OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
        controller.add(form);
        OrderData data = controller.getAll().get(0);
        int inventory1 = data.getOrder().get(0).getQuantity();

        // Empty order items
        try{orderItemFormList = new ArrayList<>();
            controller.update(testUtilHelper.getDummyOrderFrom(orderItemFormList), data.getId());}
        catch(ApiException e){assertEquals("Order does not contain any item", e.getMessage());}

    }

    @Test
    public void testSadPathUpdateOrderDuplicateItem() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 1000, 100.0));
        OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
        controller.add(form);

        OrderData data = controller.getAll().get(0);
        int inventory1 = data.getOrder().get(0).getQuantity();

        // Duplicate Order items
        try{orderItemFormList = new ArrayList<>();
            orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 10, 10.0));
            orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode", 10, 1.0));
            controller.update(testUtilHelper.getDummyOrderFrom(orderItemFormList), data.getId());}
        catch(ApiException e){assertEquals("Found duplicate order item", e.getMessage());}

    }

    @Test
    public void testSadPathUpdateOrderQuantity() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 1000, 100.0));
        OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
        controller.add(form);

        OrderData data = controller.getAll().get(0);
        int inventory1 = data.getOrder().get(0).getQuantity();

        // Quantity null
        try{orderItemFormList = new ArrayList<>();
            orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", (Integer) null, 10.0));
            controller.update(testUtilHelper.getDummyOrderFrom(orderItemFormList), data.getId());}
        catch(ApiException e){assertEquals("Quantity cannot be empty", e.getMessage());}

        // Quantity not positive
        try{orderItemFormList = new ArrayList<>();
            orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", -10, 10.0));
            orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 10, 1.0));
            controller.update(testUtilHelper.getDummyOrderFrom(orderItemFormList), data.getId());}
        catch(ApiException e){assertEquals("Quantity must be positive", e.getMessage());}


        int quantity = inventoryDao.selectAll().get(0).getQuantity()+500;
        try{orderItemFormList = new ArrayList<>();
            orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 100000, 10.0));
            orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 10, 1.0));
            controller.update(testUtilHelper.getDummyOrderFrom(orderItemFormList), data.getId());}
        catch(ApiException e){assertEquals("Quantity cannot be greater than "+ quantity, e.getMessage());}

    }

    @Test
    public void testSadPathUpdateOrderSellingPrice() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 1000, 100.0));
        OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
        controller.add(form);

        OrderData data = controller.getAll().get(0);
        int inventory1 = data.getOrder().get(0).getQuantity();


        // Selling Price Less than 0
        try{orderItemFormList = new ArrayList<>();
            orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 10, -10.0));
            orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 10, 1.0));
            controller.update(testUtilHelper.getDummyOrderFrom(orderItemFormList), data.getId());}
        catch(ApiException e){assertEquals("Selling price cannot be negative", e.getMessage());}

        double price = productDao.select(inventoryDao.selectAll().get(0).getProductId()).getMrp();
        try{orderItemFormList = new ArrayList<>();
            orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 10, 100000.0));
            orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 10, 1.0));
            controller.update(testUtilHelper.getDummyOrderFrom(orderItemFormList), data.getId());}
        catch(ApiException e){assertEquals("Selling Price cannot be greater than MRP: "+ Double.toString(price), e.getMessage());}
    }


    @Test
    public void testSadPathUpdateOrderBarcodeNotExists() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 1000, 100.0));
        OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
        controller.add(form);

        OrderData data = controller.getAll().get(0);
        int inventory1 = data.getOrder().get(0).getQuantity();

        // Barcode Check
        try{orderItemFormList = new ArrayList<>();
            orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode3", 10, 10.0));
            orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 10, 1.0));
            controller.update(testUtilHelper.getDummyOrderFrom(orderItemFormList), data.getId());}
        catch(ApiException e){assertEquals("Product with given barcode does not exists, Barcode: " + "barcode3", e.getMessage());}

    }

    @Test
    public void testOrderItemValidateQuantity(){

        // Quantity null
        try{OrderItemForm orderItemForm = testUtilHelper.getOrderItemForm("barcode", (Integer)null , 50.0);
            controller.validate(orderItemForm);}
        catch(ApiException e){assertEquals("Quantity cannot be empty", e.getMessage());}

        // Quantity not positive
        try{OrderItemForm orderItemForm = testUtilHelper.getOrderItemForm("barcode", 0 , 50.0);
            controller.validate(orderItemForm);}
        catch(ApiException e){assertEquals("Quantity cannot be empty", e.getMessage());}

        int quantity = inventoryDao.selectAll().get(0).getQuantity();
        try{OrderItemForm orderItemForm = testUtilHelper.getOrderItemForm("barcode", (Integer) 2000, 50.0);
            controller.validate(orderItemForm);}
        catch(ApiException e){assertEquals("Quantity cannot be greater than "+ quantity, e.getMessage());}

    }
    @Test
    public void testOrderItemValidateBarcodeNotExists(){
        // Barcode Check
        try{OrderItemForm orderItemForm = testUtilHelper.getOrderItemForm("barcode3", 100 , 50.0);
            controller.validate(orderItemForm);}
        catch(ApiException e){assertEquals("Product with given barcode does not exists, Barcode: " + "barcode3", e.getMessage());}
    }

    @Test
    public void testOrderItemValidateSellingPrice(){

        // Seeling Price Less than 0
        try{OrderItemForm orderItemForm = testUtilHelper.getOrderItemForm("barcode", 100 , -50.0);
            controller.validate(orderItemForm);}
        catch(ApiException e){assertEquals("Selling price cannot be negative", e.getMessage());}

        double price = productDao.select(inventoryDao.selectAll().get(0).getProductId()).getMrp();
        try{OrderItemForm orderItemForm = testUtilHelper.getOrderItemForm("barcode", (Integer) 200, 5000.0);
            controller.validate(orderItemForm);}
        catch(ApiException e){assertEquals("Selling Price cannot be greater than MRP: "+ Double.toString(price), e.getMessage());}

    }


    @Test
    public void testUpdateOrderItemValidateQuantity() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 1000, 100.0));
        OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
        controller.add(form);
        Integer id = controller.getAll().get(0).getId();
        // Quantity null
        try{OrderItemForm orderItemForm = testUtilHelper.getOrderItemForm("barcode", (Integer)null , 50.0);
            controller.editValidate(orderItemForm, id);}
        catch(ApiException e){assertEquals("Quantity cannot be empty", e.getMessage());}

        // Quantity not positive
        try{OrderItemForm orderItemForm = testUtilHelper.getOrderItemForm("barcode", 0 , 50.0);
            controller.editValidate(orderItemForm, id);}
        catch(ApiException e){assertEquals("Quantity cannot be empty", e.getMessage());}

        int quantity = inventoryDao.selectAll().get(0).getQuantity()+500;
        try{OrderItemForm orderItemForm = testUtilHelper.getOrderItemForm("barcode", (Integer) 2000, 50.0);
            controller.editValidate(orderItemForm, id);}
        catch(ApiException e){assertEquals("Quantity cannot be greater than "+ quantity, e.getMessage());}

    }
    @Test
    public void testUpdateOrderItemValidateBarcodeNotExists() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 1000, 100.0));
        OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
        controller.add(form);
        Integer id = controller.getAll().get(0).getId();
        // Barcode Check
        try{OrderItemForm orderItemForm = testUtilHelper.getOrderItemForm("barcode3", 100 , 50.0);
            controller.editValidate(orderItemForm, id);}
        catch(ApiException e){assertEquals("Product with given barcode does not exists, Barcode: " + "barcode3", e.getMessage());}
    }

    @Test
    public void testUpdateOrderItemValidateSellingPrice() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 1000, 100.0));
        OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
        controller.add(form);
        Integer id = controller.getAll().get(0).getId();

        // Seeling Price Less than 0
        try{OrderItemForm orderItemForm = testUtilHelper.getOrderItemForm("barcode", 100 , -50.0);
            controller.editValidate(orderItemForm, id);}
        catch(ApiException e){assertEquals("Selling price cannot be negative", e.getMessage());}

        double price = productDao.select(inventoryDao.selectAll().get(0).getProductId()).getMrp();
        try{OrderItemForm orderItemForm = testUtilHelper.getOrderItemForm("barcode", (Integer) 200, 5000.0);
            controller.editValidate(orderItemForm, id);}
        catch(ApiException e){assertEquals("Selling Price cannot be greater than MRP: "+ Double.toString(price), e.getMessage());}

    }

    @Test
    public void testAddInvoice() throws ApiException {
        InvoiceClient invoiceClient = Mockito.mock(InvoiceClient.class);
        Mockito.when(invoiceClient.generateInvoice(Mockito.any())).thenReturn(Base64.getEncoder().encodeToString(new byte[12]));
        invoiceClientApi.setInvoiceClient(invoiceClient);
        orderFlow.setInvoiceClientApi(invoiceClientApi);
        controller.setOrderDto(orderDto);
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 1000, 100.0));
        OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
        controller.add(form);
        InvoiceForm invoiceForm = new InvoiceForm();
        invoiceForm.setOrderId(controller.getAll().get(0).getId());
        controller.generateInvoice(invoiceForm);
        assertEquals(true, controller.getAll().get(0).getIsInvoiceGenerated());
    }
    @Test
    public void testSadPathAddInvoice() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 1000, 100.0));
        OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
        controller.add(form);
        InvoiceForm invoiceForm = new InvoiceForm();
        invoiceForm.setOrderId(controller.getAll().get(0).getId());
        try {
            controller.generateInvoice(invoiceForm);
        }catch (ApiException e){
            assertEquals("Unable to generate invoice", e.getMessage());
        }
    }
    @Test
    public void testGetInvoice() throws ApiException {
        InvoiceClient invoiceClient = Mockito.mock(InvoiceClient.class);
        Mockito.when(invoiceClient.generateInvoice(Mockito.any())).thenReturn(Base64.getEncoder().encodeToString(new byte[12]));
        invoiceClientApi.setInvoiceClient(invoiceClient);
        orderFlow.setInvoiceClientApi(invoiceClientApi);
        controller.setOrderDto(orderDto);
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(testUtilHelper.getOrderItemForm("barcode", 500, 50.0));
        orderItemFormList.add(testUtilHelper.getOrderItemForm("bArcode2", 1000, 100.0));
        OrderForm form = testUtilHelper.getDummyOrderFrom(orderItemFormList);
        controller.add(form);
        InvoiceForm invoiceForm = new InvoiceForm();
        invoiceForm.setOrderId(controller.getAll().get(0).getId());
        controller.generateInvoice(invoiceForm);
        ResponseEntity<byte[]> response= controller.getInvoice(invoiceForm.getOrderId());
        assertNotEquals(0, response.toString().length());
    }
}
