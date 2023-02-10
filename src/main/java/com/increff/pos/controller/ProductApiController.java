package com.increff.pos.controller;


import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
public class ProductApiController {

    @Autowired
    private ProductDto dto;

    @ApiOperation(value= "Adds a Product")
    @RequestMapping(path = "/api/product", method = RequestMethod.POST)
    public void add(@RequestBody ProductForm form) throws ApiException {
        dto.add(form);
    }

    @ApiOperation(value = "Deletes a product")
    @RequestMapping(path = "/api/product/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Integer id) {
        dto.delete(id);
    }


    @ApiOperation(value = "Gets a product by ID")
    @RequestMapping(path = "/api/product/{id}", method = RequestMethod.GET)
    public ProductData get(@PathVariable Integer id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "Gets list of all products")
    @RequestMapping(path = "/api/product", method = RequestMethod.GET)
    public List<ProductData> getAll() throws ApiException {
        return dto.getAll();
    }

    @ApiOperation(value = "Updates a product")
    @RequestMapping(path = "/api/product/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable Integer id, @RequestBody ProductForm f) throws ApiException {
        dto.update(id, f);
    }
}
