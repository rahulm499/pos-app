package com.increff.pos.flow;

import com.increff.pos.model.data.InvoiceData;
import com.increff.pos.model.data.InvoiceItemData;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.api.*;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.*;

import static com.increff.pos.helper.OrderHelperUtil.convertInvoiceData;
import static com.increff.pos.helper.OrderHelperUtil.convertInvoiceItemData;


@Service
public class OrderFlow {

    @Autowired
    private OrderApi orderApi;
    @Autowired
    private InventoryApi inventoryApi;

    @Autowired
    private ProductApi productApi;

    @Autowired
    @Setter
    private InvoiceClientApi invoiceClientApi;


    @Transactional(rollbackFor = ApiException.class)
    public void add(Map<String, OrderItemPojo> orderItemPojoList, OrderPojo orderPojo) throws ApiException {
        orderApi.add(orderPojo);
        for (Map.Entry<String, OrderItemPojo> e : orderItemPojoList.entrySet()) {
            String barcode = e.getKey();
            OrderItemPojo orderItemPojo = e.getValue();
            orderItemPojo.setProductId(productApi.getCheckBarcode(barcode).getId());
            validateOrderItem(orderItemPojo, null, barcode);
            orderItemPojo.setOrderId(orderPojo.getId());
            orderApi.addOrderItem(orderItemPojo);
            updateAddOrderInventory(orderItemPojo);
        }
    }


    @Transactional(rollbackFor = ApiException.class)
    public void update(Map<String, OrderItemPojo> orderItemPojoList, Integer orderId) throws ApiException {
        // Validate Order
        validateOrder(orderId);
        //Delete Order Items
        deleteOrderItems(orderId, orderItemPojoList);
        //Add Order Items
        for (Map.Entry<String, OrderItemPojo> entry : orderItemPojoList.entrySet()) {
            OrderItemPojo orderItemPojo = entry.getValue();
            String barcode = entry.getKey();
            orderItemPojo.setProductId(productApi.getCheckBarcode(barcode).getId());
            //Validate Order Items
            validateOrderItem(orderItemPojo, orderId, barcode);
            OrderItemPojo existingOrderItemPojo = orderApi.getByOrderItemProductId(orderId, orderItemPojo.getProductId());
            InventoryPojo inventoryPojo = inventoryApi.getByProduct(orderItemPojo.getProductId());
            addUpdateOrderItem(existingOrderItemPojo, inventoryPojo, orderItemPojo, orderId);
        }
    }


    public Map<Integer, String> getOrderIdByBarcodeMap(Integer id) throws ApiException {
        Map<Integer, String> map = new HashMap<>();
        List<OrderItemPojo> orderItemPojoList = orderApi.getByOrderItemId(id);
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            map.put(orderItemPojo.getId(), productApi.getCheck(orderItemPojo.getProductId()).getBarcode());
        }
        return map;
    }

    // INVOICE METHODS

    @Transactional(rollbackFor = ApiException.class)
    public String generatePdf(Integer id) throws ApiException {
        try {
            InvoiceData invoiceData = getInvoiceData(id);
            String pdfStream = invoiceClientApi.generateInvoice(invoiceData);
            storeInvoice(pdfStream, id);
            orderApi.updateInvoice(id);
            return pdfStream;
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public InvoiceData getInvoiceData(Integer id) throws ApiException {
        Map<Integer, String> orderItemsBarcode = getOrderIdByBarcodeMap(id);
        List<OrderItemPojo> orderItemPojoList = orderApi.getByOrderItemId(id);
        return convertInvoiceDataList(orderItemsBarcode, orderItemPojoList, orderApi.getCheck(id));
    }

    @Transactional(rollbackFor = ApiException.class)
    public void storeInvoice(String pdfStream, Integer id) throws ApiException {
        String filePath = "../.pdfFile\\order_" + id + ".pdf";
        byte[] decodedBytes = Base64.getDecoder().decode(pdfStream);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePath);
            fileOutputStream.write(decodedBytes);
            fileOutputStream.close();
        } catch (IOException e) {
            throw new ApiException("Unable to store invoice");
        }
    }


    public byte[] downloadPdf(Integer id) throws ApiException {
        Path pdf = Paths.get("../.pdfFile/order_" + id + ".pdf");
        byte[] contents = null;
        try {
            contents = Files.readAllBytes(pdf);
        } catch (IOException e) {
            throw new ApiException("Unable to fetch invoice pdf");
        }
        return contents;
    }

    public void validateOrderItem(OrderItemPojo orderItemPojo, Integer orderId, String barcode) throws ApiException {
        int productId = productApi.getCheckBarcode(barcode).getId();
        productApi.getCheck(productId);
        int quantity = inventoryApi.getByProduct(productId).getQuantity();
        if (orderId != null) {
            OrderItemPojo orderItemPojo1 = orderApi.getByOrderItemProductId(orderId, productId);
            if (orderItemPojo1 != null) quantity += orderItemPojo1.getQuantity();
        }
        double price = productApi.getCheck(productId).getMrp();
        String exp1 = "Quantity cannot be greater than " + quantity;
        String exp2 = "Selling Price cannot be greater than MRP: " + Double.toString(price);
        if (orderItemPojo.getQuantity() > quantity) {
            throw new ApiException(exp1);
        }
        if (orderItemPojo.getSellingPrice() > price) {
            throw new ApiException(exp2);
        }
    }


    private void updateAddOrderInventory(OrderItemPojo orderItemPojo) {
        InventoryPojo inventoryPojo = inventoryApi.getByProduct(orderItemPojo.getProductId());
        inventoryPojo.setQuantity(inventoryPojo.getQuantity() - orderItemPojo.getQuantity());
    }

    private void addUpdateOrderItem(OrderItemPojo existingOrderItemPojo, InventoryPojo inventoryPojo,
                                    OrderItemPojo orderItemPojo, Integer orderId) throws ApiException {
        if (existingOrderItemPojo != null) {
            inventoryPojo.setQuantity(inventoryPojo.getQuantity() + existingOrderItemPojo.getQuantity() - orderItemPojo.getQuantity());
            orderApi.update(existingOrderItemPojo.getId(), orderItemPojo);
        } else {
            inventoryPojo.setQuantity(inventoryPojo.getQuantity() - orderItemPojo.getQuantity());
            orderItemPojo.setOrderId(orderId);
            orderApi.addOrderItem(orderItemPojo);
        }
    }

    private void deleteOrderItems(Integer id, Map<String, OrderItemPojo> orderItemPojoList) throws ApiException {
        List<OrderItemPojo> existingOrderItems = orderApi.getByOrderItemId(id);
        Set<OrderItemPojo> existingOrderItemsSet = new HashSet<>(existingOrderItems);
        Set<Integer> productIds = new HashSet<>();
        orderItemPojoList.forEach((barcode, orderItemPojo) -> {
            productIds.add(orderItemPojo.getProductId());
        });
        for (OrderItemPojo existingOrderItemPojo : existingOrderItemsSet) {
            if (!productIds.contains(existingOrderItemPojo.getProductId())) {
                InventoryPojo inventoryPojo = inventoryApi.getByProduct(existingOrderItemPojo.getProductId());
                inventoryPojo.setQuantity(inventoryPojo.getQuantity() + existingOrderItemPojo.getQuantity());
                orderApi.deleteOrderItem(existingOrderItemPojo.getId());
            }
        }
    }

    private InvoiceData convertInvoiceDataList(Map<Integer, String> orderItemsBarcode,
                                               List<OrderItemPojo> orderItemPojoList, OrderPojo orderPojo) throws ApiException {
        List<InvoiceItemData> invoiceItemDataList = new ArrayList<>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            String product = productApi.getCheckBarcode(orderItemsBarcode.get(orderItemPojo.getId())).getName();
            InvoiceItemData invoiceItemData = convertInvoiceItemData(orderItemPojo, orderItemsBarcode.get(orderItemPojo.getId()), product);
            invoiceItemDataList.add(invoiceItemData);
        }
        InvoiceData invoiceData = convertInvoiceData(orderPojo, String.valueOf(orderPojo.getCreated_at().withZoneSameInstant(ZoneId.of("UTC"))));
        invoiceData.setInvoiceItemDataList(invoiceItemDataList);
        return invoiceData;
    }

    private void validateOrder(Integer id) throws ApiException {
        if (orderApi.getCheck(id).getIsInvoiceGenerated()) {
            throw new ApiException("Unable to edit order");
        }
    }

}
