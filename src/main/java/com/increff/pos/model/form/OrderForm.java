package com.increff.pos.model.form;

import com.increff.pos.model.data.OrderItemData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderForm {
    private  List<OrderItemForm> orderItems;

}
