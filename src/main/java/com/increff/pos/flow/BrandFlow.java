package com.increff.pos.flow;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class BrandFlow {

    @Autowired
    private BrandService service;

    @Transactional(rollbackOn = ApiException.class)
    public void add(BrandPojo brandPojo) throws ApiException {
        validate(brandPojo);
        service.add(brandPojo);
    }
    @Transactional(rollbackOn = ApiException.class)
    public void update(Integer id, BrandPojo brandPojo) throws ApiException {
        validate(brandPojo);
        service.update(id, brandPojo);
    }


    protected void validate(BrandPojo brandPojo) throws ApiException {
        if(service.getCheckBrandCategory(brandPojo.getBrand(), brandPojo.getCategory())!=null){
            throw new ApiException("Brand Category Pair already exists");
        }
    }

}
