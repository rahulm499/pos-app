package com.increff.pos.flow;

import com.increff.pos.model.data.InvoiceData;
import com.increff.pos.model.data.InvoiceItemData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.*;
import com.increff.pos.helper.OrderHelperUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.util.*;

import static com.increff.pos.helper.OrderHelperUtil.convertInvoiceData;
import static com.increff.pos.helper.OrderHelperUtil.convertInvoiceItemData;


@Service
public class OrderFlow {
    @Autowired
    private OrderItemApi orderItemApi;
    @Autowired
    private OrderApi orderApi;
    @Autowired
    @Setter
    private InventoryApi inventoryApi;

    @Autowired
    private ProductApi productApi;
    @Autowired
    private ProductFlow productFlow;




    @Transactional(rollbackFor = ApiException.class)
    public void add(Map<Integer, OrderItemPojo> orderItemPojoList, OrderPojo orderPojo, Map<Integer, String> orderItemBarcode) throws ApiException {
        for (Map.Entry<Integer, OrderItemPojo> e : orderItemPojoList.entrySet()) {
            Integer key = e.getKey();
            OrderItemPojo value = e.getValue();
            String barcode = orderItemBarcode.get(key);
            value.setProductId(productFlow.getProductId(barcode));
            validateOrderItem(value, null, barcode);
        }
        orderApi.add(orderPojo);
        for (Map.Entry<Integer, OrderItemPojo> entry : orderItemPojoList.entrySet()) {
            OrderItemPojo orderItemPojo = entry.getValue();
            orderItemPojo.setOrderId(orderPojo.getId());
            orderItemApi.add(orderItemPojo);
            InventoryPojo inventoryPojo = inventoryApi.getByProduct(orderItemPojo.getProductId());
            inventoryPojo.setQuantity(inventoryPojo.getQuantity() - orderItemPojo.getQuantity());
            inventoryApi.update(inventoryPojo.getId(), inventoryPojo);
        }
    }

    @Transactional(rollbackFor = ApiException.class)
    public void update(Map<Integer, OrderItemPojo> orderItemPojoList, Integer id, Map<Integer, String> orderItemBarcode) throws ApiException {
        // Validate Order items
        validateOrder(id);
        for (Map.Entry<Integer, OrderItemPojo> e : orderItemPojoList.entrySet()) {
            Integer key = e.getKey();
            OrderItemPojo value = e.getValue();
            String barcode = orderItemBarcode.get(key);
            value.setProductId(productFlow.getProductId(barcode));
            validateOrderItem(value, id, barcode);
        }
        //Delete Order Items
        deleteOrderItems(id, orderItemPojoList);
        //Add Order Items
        for (Map.Entry<Integer, OrderItemPojo> entry : orderItemPojoList.entrySet()) {
            OrderItemPojo orderItemPojo = entry.getValue();
            OrderItemPojo orderItemPojo1 = orderItemApi.getByOrderProductId(id, orderItemPojo.getProductId());
            InventoryPojo inventoryPojo = inventoryApi.getByProduct(orderItemPojo.getProductId());
            if(orderItemPojo1 != null){
                inventoryPojo.setQuantity(inventoryPojo.getQuantity() + orderItemPojo1.getQuantity() - orderItemPojo.getQuantity());
                orderItemApi.update(orderItemPojo1.getId(), orderItemPojo);
            }else {
                inventoryPojo.setQuantity(inventoryPojo.getQuantity() - orderItemPojo.getQuantity());
                orderItemPojo.setOrderId(id);
                orderItemApi.add(orderItemPojo);
            }
        }
    }
    @Transactional(rollbackFor = ApiException.class)
    public void deleteOrderItems(Integer id, Map<Integer, OrderItemPojo> orderItemPojoList) throws ApiException {
        List<OrderItemPojo> existingOrderItems = orderItemApi.getByOrderId(id);
        int flag=0;
        for(OrderItemPojo existingOrderItemPojo: existingOrderItems){
            flag=0;

            for(int i=0;i<orderItemPojoList.size();i++){
                if(existingOrderItemPojo.getProductId() == orderItemPojoList.get(i).getProductId()){
                    flag=1;
                    break;
                }
            }
            if(flag==0){
                InventoryPojo inventoryPojo = inventoryApi.getByProduct(existingOrderItemPojo.getProductId());
                inventoryPojo.setQuantity(inventoryPojo.getQuantity() + existingOrderItemPojo.getQuantity());
                orderItemApi.delete(existingOrderItemPojo.getId());
            }
        }
    }

    @Transactional(rollbackFor = ApiException.class)
    public void updateInvoiceStatus(Integer id) throws ApiException {
        orderApi.updateInvoice(id);
    }



    @Transactional(rollbackFor = ApiException.class)
    public Map<Integer, String> getOrderItemsBarcode(Integer id) throws ApiException {
        Map<Integer, String> map = new HashMap<>();
        List<OrderItemPojo> orderItemPojoList = orderItemApi.getByOrderId(id);
        for(OrderItemPojo orderItemPojo: orderItemPojoList){
            map.put(orderItemPojo.getId(), productApi.getCheck(orderItemPojo.getProductId()).getBarcode());
      }
        return map;
    }

    // INVOICE METHODS
    @Transactional(rollbackFor = ApiException.class)
    public InvoiceData generateInvoiceData(Integer id) throws ApiException {
        Map<Integer, String> orderItemsBarcode = getOrderItemsBarcode(id);
        List<OrderItemPojo> orderItemPojoList = orderItemApi.getByOrderId(id);
        return convertInvoiceDataList(orderItemsBarcode, orderItemPojoList, orderApi.get(id));
    }

    @Transactional(rollbackFor = IOException.class)
    public void storeInvoice(String pdfStream, Integer id) throws IOException {
        String filePath = "../.pdfFile\\order_"+id+".pdf";
        byte[] decodedBytes = Base64.getDecoder().decode(pdfStream);
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        fileOutputStream.write(decodedBytes);
        fileOutputStream.close();
    }

    public InvoiceData convertInvoiceDataList(Map<Integer, String> orderItemsBarcode, List<OrderItemPojo> orderItemPojoList, OrderPojo orderPojo) throws ApiException {
        List<InvoiceItemData> invoiceItemDataList = new ArrayList<>();
        int index=1;
        Double total=0.00;
        for(OrderItemPojo orderItemPojo: orderItemPojoList){
            String product = productApi.getCheckBarcode(orderItemsBarcode.get(orderItemPojo.getId())).getName();
            InvoiceItemData invoiceItemData = convertInvoiceItemData(orderItemPojo, orderItemsBarcode.get(orderItemPojo.getId()), product, index);
            total += orderItemPojo.getSellingPrice() * orderItemPojo.getQuantity();
            invoiceItemDataList.add(invoiceItemData);
        }
        InvoiceData invoiceData = convertInvoiceData(orderPojo, total, String.valueOf(orderPojo.getCreated_at().withZoneSameInstant(ZoneId.of("UTC") )));
        invoiceData.setInvoiceItemDataList(invoiceItemDataList);
        return invoiceData;
    }
    protected void validateOrder(Integer id) throws ApiException {
        if(orderApi.get(id).getIsInvoiceGenerated()){
            throw new ApiException("Unable to edit order");
        }
    }


    public void validateOrderItem(OrderItemPojo orderItemPojo, Integer orderId, String barcode) throws ApiException {
        int prodId = productFlow.getProductId(barcode);
        productApi.getCheck(prodId);
        int quantity = inventoryApi.getByProduct(prodId).getQuantity();
        if(orderId !=null) {
            OrderItemPojo orderItemPojo1 = orderItemApi.getByOrderProductId(orderId, prodId);
            if (orderItemPojo1 != null)
                quantity += orderItemPojo1.getQuantity();
        }
        double price = productApi.get(prodId).getMrp();
        String exp1 = "Quantity cannot be greater than "+ quantity;
        String exp2 = "Selling Price cannot be greater than MRP: "+ Double.toString(price);
        if(orderItemPojo.getQuantity() > quantity){
            throw new ApiException(exp1);
        }
        if(orderItemPojo.getSellingPrice() > price){
            throw new ApiException(exp2);
        }
    }
}
