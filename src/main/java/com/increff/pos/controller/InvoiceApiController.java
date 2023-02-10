package com.increff.pos.controller;

import com.increff.pos.dto.InvoiceDto;
import com.increff.pos.model.form.InvoiceForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Api
@RestController
public class InvoiceApiController {
    @Autowired
    private InvoiceDto dto;

    @ApiOperation(value= "Generates an invoice")
    @RequestMapping(path = "/api/invoice", method = RequestMethod.POST)
    public void add(@RequestBody InvoiceForm invoiceForm) throws Exception {
       dto.generatePdf(invoiceForm.getOrderId());
    }

    @ApiOperation(value= "Downloads an invoice")
    @RequestMapping(path = "/api/invoice/{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> get(@PathVariable Integer id) throws Exception {
        return dto.downloadPdf(id);
    }

}
