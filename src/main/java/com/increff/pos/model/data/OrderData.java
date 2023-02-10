package com.increff.pos.model.data;

import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class OrderData{
    private Integer id;

    private String dateTime;
    private List<OrderItemData> order;
}
