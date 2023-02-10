package com.increff.pos.controller;

import com.increff.pos.dto.OrderDto;

import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
public class OrderItemApiController {
    @Autowired
    private OrderDto dto;

    @ApiOperation(value= "Validates an Order Item")
    @RequestMapping(path = "/api/order-item", method = RequestMethod.POST)
    public void validate(@RequestBody OrderItemForm form) throws ApiException {
        dto.getCheckItem(form);
    }

}
