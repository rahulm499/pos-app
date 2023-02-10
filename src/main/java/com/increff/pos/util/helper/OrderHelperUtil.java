package com.increff.pos.util.helper;

import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class OrderHelperUtil {
    public static OrderPojo convertOrder(OrderForm f){
        OrderPojo p = new OrderPojo();
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC-4"));
        System.out.println(zonedDateTime);
        p.setCreated_at(zonedDateTime);
        System.out.println(p.getCreated_at());
        return p;
    }
    public static OrderForm convertOrder(OrderPojo p){
        OrderForm f = new OrderForm();
        return f;
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
}
