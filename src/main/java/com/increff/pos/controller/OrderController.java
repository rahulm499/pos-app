package com.increff.pos.controller;


import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.form.InvoiceForm;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.api.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
public class OrderController {

    @Autowired
    @Setter
    private OrderDto orderDto;


    @ApiOperation(value = "Adds an Order")
    @RequestMapping(path = "/api/order", method = RequestMethod.POST)
    public void add(@RequestBody OrderForm form) throws ApiException {
        orderDto.add(form);
    }

    @ApiOperation(value = "Updates an Order")
    @RequestMapping(path = "/api/order/{id}", method = RequestMethod.PUT)
    public void update(@RequestBody OrderForm form, @PathVariable Integer id) throws ApiException {
        orderDto.update(form, id);
    }

    @ApiOperation(value = "Gets a order by ID")
    @RequestMapping(path = "/api/order/{id}", method = RequestMethod.GET)
    public OrderData get(@PathVariable Integer id) throws ApiException {
        return orderDto.get(id);
    }

    @ApiOperation(value = "Gets list of all orders")
    @RequestMapping(path = "/api/order", method = RequestMethod.GET)
    public List<OrderData> getAll() throws ApiException {
        return orderDto.getAll();
    }

    @ApiOperation(value = "Generates an invoice")
    @RequestMapping(path = "/api/invoice", method = RequestMethod.POST)
    public void generateInvoice(@RequestBody InvoiceForm invoiceForm) throws ApiException {
        orderDto.generatePdf(invoiceForm.getOrderId());
    }

    @ApiOperation(value = "Downloads an invoice")
    @RequestMapping(path = "/api/invoice/{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getInvoice(@PathVariable Integer id) throws ApiException {
        byte[] contents = orderDto.downloadPdf(id);
        return getInvoiceSetHeaders(contents);
    }

    @ApiOperation(value = "Validates an existing order Item")
    @RequestMapping(path = "/api/order-item/{id}", method = RequestMethod.POST)
    public void editValidate(@RequestBody OrderItemForm form, @PathVariable Integer id) throws ApiException {
        orderDto.getCheckOrderItem(form, id);
    }

    @ApiOperation(value = "Validates an Order Item")
    @RequestMapping(path = "/api/order-item", method = RequestMethod.POST)
    public void validate(@RequestBody OrderItemForm form) throws ApiException {
        orderDto.getCheckOrderItem(form, null);
    }

    private ResponseEntity<byte[]> getInvoiceSetHeaders(byte[] contents) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "invoice.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(contents, headers, HttpStatus.OK);
    }


}
