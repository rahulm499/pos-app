package com.increff.pos.flow;

import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import com.increff.pos.util.helper.OrderHelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderFlow {
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductService productService;

    @Transactional(rollbackOn = ApiException.class)
    public List<OrderItemPojo> convertOrderItem(List<OrderItemForm> orderItemFormList) throws ApiException {
        List<OrderItemPojo> orderItemPojos = new ArrayList<>();
        for (OrderItemForm orderItemForm : orderItemFormList) {
            OrderItemPojo b = OrderHelperUtil.convertOrderItem(orderItemForm);
            b.setProductId(productService.getCheckBarcode(orderItemForm.getBarcode()).getId());
            validate(b);
            orderItemPojos.add(b);
        }
        return orderItemPojos;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void add(List<OrderItemPojo> orderItemPojoList, Integer orderId) throws ApiException {
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            orderItemPojo.setOrderId(orderId);
            orderItemService.add(orderItemPojo);
            InventoryPojo inventoryPojo = inventoryService.getByProduct(orderItemPojo.getProductId());
            inventoryPojo.setQuantity(inventoryPojo.getQuantity() - orderItemPojo.getQuantity());
            inventoryService.update(inventoryPojo.getId(), inventoryPojo);
        }
    }
    @Transactional(rollbackOn = ApiException.class)
    public void delete(Integer id) throws ApiException {
        List<OrderItemPojo> orderItemPojoList =orderItemService.getByOrderId(id);
        for (OrderItemPojo orderItemPojo : orderItemPojoList){
            orderItemService.delete(orderItemPojo.getId());
        }
        orderService.delete(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updateInvoiceStatus(Integer id) throws ApiException {
        orderService.update(id);
    }



    @Transactional(rollbackOn = ApiException.class)
    public List<OrderItemData> getOrderItems(Integer id) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderItemService.getByOrderId(id);
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for(OrderItemPojo orderItemPojo: orderItemPojoList){
            OrderItemData orderItemData = OrderHelperUtil.convertOrderItem(orderItemPojo);
            orderItemData.setBarcode(productService.getCheck(orderItemPojo.getProductId()).getBarcode());
            orderItemDataList.add(orderItemData);
        }
        return orderItemDataList;
    }


    public void validate(OrderItemPojo orderItemPojo) throws ApiException {
        productService.getCheck(orderItemPojo.getProductId());
        int quantity = inventoryService.getByProduct(orderItemPojo.getProductId()).getQuantity();
        String exept1 = "Quantity cannot be greater than inventory: "+ quantity;
        if(orderItemPojo.getQuantity() > quantity){
            throw new ApiException(exept1);
        }
    }
}
