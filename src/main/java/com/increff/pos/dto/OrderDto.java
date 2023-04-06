package com.increff.pos.dto;

import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.*;
import com.increff.pos.api.*;
import com.increff.pos.helper.OrderHelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

import static com.increff.pos.helper.OrderHelperUtil.*;

@Service
public class OrderDto {

    @Autowired
    private OrderApi orderApi;

    @Autowired
    private OrderFlow orderFlow;


    public void add(OrderForm form) throws ApiException {
        validateOrderForm(form);
        normalizeOrder(form);
        Map<String, OrderItemPojo> orderItemPojoList = new HashMap<>();
        for (OrderItemForm orderItemForm : form.getOrderItems()) {
            orderItemPojoList.put(orderItemForm.getBarcode(), OrderHelperUtil.convertOrderItem(orderItemForm));
        }
        OrderPojo orderPojo = OrderHelperUtil.convertOrder(form);
        orderFlow.add(orderItemPojoList, orderPojo);
    }

    public void update(OrderForm form, Integer id) throws ApiException {
        validateOrderForm(form);
        normalizeOrder(form);
        Map<String, OrderItemPojo> orderItemPojoList = new HashMap<>();
        for (OrderItemForm orderItemForm : form.getOrderItems()) {
            orderItemPojoList.put(orderItemForm.getBarcode(), OrderHelperUtil.convertOrderItem(orderItemForm));
        }
        orderFlow.update(orderItemPojoList, id);
    }


    public OrderData get(Integer id) throws ApiException {
        Map<Integer, String> orderItemsBarcode = orderFlow.getOrderIdByBarcodeMap(id);
        List<OrderItemPojo> orderItemPojoList = orderApi.getByOrderItemId(id);
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            OrderItemData orderItemData = OrderHelperUtil.convertOrderItem(orderItemPojo);
            orderItemData.setBarcode(orderItemsBarcode.get(orderItemPojo.getId()));
            orderItemDataList.add(orderItemData);
        }
        return OrderHelperUtil.convertOrderData(orderItemDataList, orderApi.getCheck(id));
    }

    public List<OrderData> getAll() throws ApiException {
        List<OrderPojo> orderPojoList = orderApi.getAll();
        List<OrderData> data = new ArrayList<>();
        for (OrderPojo orderPojo : orderPojoList) {
            OrderData d = get(orderPojo.getId());
            data.add(d);
        }
        return data;
    }


    public String generatePdf(Integer id) throws ApiException {
        return orderFlow.generatePdf(id);
    }

    public byte[] downloadPdf(Integer id) throws ApiException {
        return orderFlow.downloadPdf(id);
    }

    public void getCheckOrderItem(OrderItemForm form, Integer id) throws ApiException {
        validateOrderItemForm(form);
        normalizeOrderItem(form);
        OrderItemPojo orderItemPojo = OrderHelperUtil.convertOrderItem(form);
        orderFlow.validateOrderItem(orderItemPojo, id, form.getBarcode());
    }


}
