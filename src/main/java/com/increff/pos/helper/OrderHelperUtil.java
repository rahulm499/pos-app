package com.increff.pos.helper;

import com.increff.pos.model.data.InvoiceData;
import com.increff.pos.model.data.InvoiceItemData;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.api.ApiException;
import com.increff.pos.util.StringUtil;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class OrderHelperUtil {
    public static OrderPojo convertOrder(OrderForm f){
        OrderPojo p = new OrderPojo();
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC-4"));
        p.setCreated_at(zonedDateTime);
        p.setIsInvoiceGenerated(Boolean.FALSE);
        return p;
    }

    public static OrderItemPojo convertOrderItem(OrderItemForm f){
        OrderItemPojo p = new OrderItemPojo();
        p.setQuantity(f.getQuantity());
        p.setSellingPrice(f.getSellingPrice());
        return p;
    }
    public static OrderItemData convertOrderItem(OrderItemPojo p){
        OrderItemData orderItemData = new OrderItemData();
        orderItemData.setQuantity(p.getQuantity());
        orderItemData.setSellingPrice(p.getSellingPrice());
        return orderItemData;
    }
    public static OrderData convertOrderData(List<OrderItemData> orderItemDataList, OrderPojo orderPojo){
        OrderData data = new OrderData();
        data.setId(orderPojo.getId());
        data.setDateTime(String.valueOf(orderPojo.getCreated_at().withZoneSameInstant(ZoneId.of("UTC") )));
        data.setOrder(orderItemDataList);
        data.setIsInvoiceGenerated(orderPojo.getIsInvoiceGenerated());
        return data;
    }
    public static InvoiceData convertInvoiceData(OrderPojo orderPojo, String date){
        InvoiceData invoiceData = new InvoiceData();
        invoiceData.setOrderId(orderPojo.getId());
        String time = date.split("T")[1];
        time = time.split("Z")[0];
        date = date.split("T")[0];
        invoiceData.setDate(date);
        invoiceData.setTime(time);
        invoiceData.setTotalAmount(0.0);
        return invoiceData;
    }
    public static InvoiceItemData convertInvoiceItemData(OrderItemPojo orderItemPojo,String barcode, String product){
        InvoiceItemData invoiceItemData = new InvoiceItemData();
        invoiceItemData.setQuantity(orderItemPojo.getQuantity());
        invoiceItemData.setBarcode(barcode);
        invoiceItemData.setUnitPrice(orderItemPojo.getSellingPrice());
        if(product.length()>20)
            invoiceItemData.setProduct(product.substring(0, 20));
        else
            invoiceItemData.setProduct(product);
        invoiceItemData.setAmount(orderItemPojo.getSellingPrice()*orderItemPojo.getQuantity());
        return invoiceItemData;
    }


    public static void normalizeOrder(OrderForm form){
        List<OrderItemForm> orderItemFormList = form.getOrderItems();
        orderItemFormList.forEach(OrderHelperUtil::normalizeOrderItem);
        form.setOrderItems(orderItemFormList);
    }
    public static void normalizeOrderItem(OrderItemForm form){
        DecimalFormat df = new DecimalFormat("#.##");
        form.setSellingPrice(Double.parseDouble(df.format(form.getSellingPrice())));
        form.setBarcode(StringUtil.toLowerCase(form.getBarcode()));
    }
    public static void validateOrderForm(OrderForm form) throws ApiException {

        if(form.getOrderItems().size() == 0){
            throw new ApiException("Order does not contain any item");
        }
        Set<String> set = new HashSet<String>();
        for(OrderItemForm orderItemForm: form.getOrderItems()){
            if(set.contains(orderItemForm.getBarcode())){
                throw new ApiException("Found duplicate order item");
            }
            set.add(orderItemForm.getBarcode());
            validateOrderItemForm(orderItemForm);
        }
    }

    public static void validateOrderItemForm(OrderItemForm form) throws ApiException {
        if(form.getBarcode() == null || form.getBarcode().isEmpty()){
            throw new ApiException("Barcode cannot be empty");
        }
        if(form.getQuantity() == null || form.getQuantity().intValue() == 0){
            throw new ApiException("Quantity cannot be empty");
        }
        if(form.getSellingPrice() == null || form.getSellingPrice().intValue() == 0){
            throw new ApiException("Selling price cannot be empty");
        }
        if(form.getQuantity() <= 0){
            throw new ApiException("Quantity must be positive");
        }
        if(form.getSellingPrice()<0){
            throw new ApiException("Selling price cannot be negative");
        }
    }
}
