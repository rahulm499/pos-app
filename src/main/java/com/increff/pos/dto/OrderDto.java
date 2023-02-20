package com.increff.pos.dto;

import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import com.increff.pos.util.StringUtil;
import com.increff.pos.util.helper.OrderHelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderDto {

    @Autowired
    private OrderItemApiService orderItemApiService;
    @Autowired
    private OrderApiService orderApiService;
    @Autowired
    private InventoryApiService inventoryApiService;

    @Autowired
    private ProductApiService productApiService;
    @Autowired
    private OrderFlow orderFlow;

    @Transactional(rollbackFor = ApiException.class)
    public void add(OrderForm form) throws ApiException {
        normalizeOrder(form);
        validateOrderForm(form);
        List<OrderItemPojo> orderItemPojo = orderFlow.convertOrderItem(form.getOrder());
        OrderPojo orderPojo = OrderHelperUtil.convertOrder(form);
        orderFlow.add(orderItemPojo, orderPojo);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void delete(Integer id) throws ApiException {
        orderFlow.delete(id);
    }

    @Transactional(rollbackFor = ApiException.class)
    public OrderData get(Integer id) throws ApiException {
        return convertOrderForm(orderFlow.getOrderItems(id), orderApiService.get(id));
    }
    @Transactional(rollbackFor = ApiException.class)
    public List<OrderData> getAll() throws ApiException {
        List<OrderPojo> orderPojoList = orderApiService.getAll();
        List<OrderData> data = new ArrayList<>();
        for(OrderPojo orderPojo: orderPojoList){
            OrderData d = get(orderPojo.getId());
            data.add(d);
        }
        return data;
    }

    public void getCheckItem(OrderItemForm form) throws ApiException {
        normalizeOrderItem(form);
        validateOrderItemForm(form);
        OrderItemPojo orderItemPojo = OrderHelperUtil.convertOrderItem(form);
        orderItemPojo.setProductId(productApiService.getCheckBarcode(form.getBarcode()).getId());
        orderFlow.validate(orderItemPojo);
    }

    protected OrderData convertOrderForm(List<OrderItemData> orderItemDataList, OrderPojo orderPojo){
        OrderData data = new OrderData();
        data.setId(orderPojo.getId());
        data.setDateTime(String.valueOf(orderPojo.getCreated_at().withZoneSameInstant(ZoneId.of("UTC") )));
        data.setOrder(orderItemDataList);
        data.setIsInvoiceGenerated(orderPojo.getIsInvoiceGenerated());
        return data;
    }
    protected void normalizeOrder(OrderForm form){
        List<OrderItemForm> orderItemFormList = form.getOrder();
        orderItemFormList.forEach(orderItemForm -> {
            normalizeOrderItem(orderItemForm);
        });
        form.setOrder(orderItemFormList);
    }
    protected void normalizeOrderItem(OrderItemForm form){
        form.setBarcode(StringUtil.toLowerCase(form.getBarcode()));
    }
    protected void validateOrderForm(OrderForm form) throws ApiException {
        if(form.getOrder().size() == 0){
            throw new ApiException("Order does not contain any item");
        }
        for(OrderItemForm orderItemForm: form.getOrder()){
            validateOrderItemForm(orderItemForm);
        }
    }
    protected void validateOrderItemForm(OrderItemForm form) throws ApiException {
        if(form.getQuantity() == null){
            throw new ApiException("Quantity cannot be NULL");
        }
        if(form.getSellingPrice() == null){
            throw new ApiException("Selling price cannot be NULL");
        }
        if(form.getQuantity() <= 0){
            throw new ApiException("Quantity must be positive");
        }
        if(form.getSellingPrice()<0){
            throw new ApiException("Selling price cannot be negative");
        }
    }
}
