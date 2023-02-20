package com.increff.pos.flow;

import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.*;
import com.increff.pos.util.helper.OrderHelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderFlow {
    @Autowired
    private OrderItemApiService orderItemApiService;
    @Autowired
    private OrderApiService orderApiService;
    @Autowired
    private InventoryApiService inventoryApiService;

    @Autowired
    private ProductApiService productApiService;

    @Transactional(rollbackFor = ApiException.class)
    public List<OrderItemPojo> convertOrderItem(List<OrderItemForm> orderItemFormList) throws ApiException {
        List<OrderItemPojo> orderItemPojos = new ArrayList<>();
        for (OrderItemForm orderItemForm : orderItemFormList) {
            OrderItemPojo b = OrderHelperUtil.convertOrderItem(orderItemForm);
            b.setProductId(productApiService.getCheckBarcode(orderItemForm.getBarcode()).getId());
            validate(b);
            orderItemPojos.add(b);
        }
        return orderItemPojos;
    }

    @Transactional(rollbackFor = ApiException.class)
    public void add(List<OrderItemPojo> orderItemPojoList, OrderPojo orderPojo) throws ApiException {
        orderApiService.add(orderPojo);
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            orderItemPojo.setOrderId(orderPojo.getId());
            orderItemApiService.add(orderItemPojo);
            InventoryPojo inventoryPojo = inventoryApiService.getByProduct(orderItemPojo.getProductId());
            inventoryPojo.setQuantity(inventoryPojo.getQuantity() - orderItemPojo.getQuantity());
            inventoryApiService.update(inventoryPojo.getId(), inventoryPojo);
        }
    }
    @Transactional(rollbackFor = ApiException.class)
    public void delete(Integer id) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderItemApiService.getByOrderId(id);
        for (OrderItemPojo orderItemPojo : orderItemPojoList){
            orderItemApiService.delete(orderItemPojo.getId());
        }
        orderApiService.delete(id);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void updateInvoiceStatus(Integer id) throws ApiException {
        orderApiService.update(id);
    }



    @Transactional(rollbackFor = ApiException.class)
    public List<OrderItemData> getOrderItems(Integer id) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderItemApiService.getByOrderId(id);
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for(OrderItemPojo orderItemPojo: orderItemPojoList){
            OrderItemData orderItemData = OrderHelperUtil.convertOrderItem(orderItemPojo);
            orderItemData.setBarcode(productApiService.getCheck(orderItemPojo.getProductId()).getBarcode());
            orderItemDataList.add(orderItemData);
        }
        return orderItemDataList;
    }


    public void validate(OrderItemPojo orderItemPojo) throws ApiException {
        productApiService.getCheck(orderItemPojo.getProductId());
        int quantity = inventoryApiService.getByProduct(orderItemPojo.getProductId()).getQuantity();
        String exept1 = "Quantity cannot be greater than existing quantity: "+ quantity;
        if(orderItemPojo.getQuantity() > quantity){
            throw new ApiException(exept1);
        }
    }
}
