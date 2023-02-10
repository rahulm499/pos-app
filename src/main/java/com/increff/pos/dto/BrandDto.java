package com.increff.pos.dto;


import com.increff.pos.flow.BrandFlow;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.util.HelperUtil;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BrandDto {
    @Autowired
    private BrandService service;
    @Autowired
    private BrandFlow flow;

    public void add(BrandForm form) throws ApiException {
        normalizeBrandForm(form);
        validateBrandForm(form);
        flow.add(HelperUtil.convertBrand(form));
    }

    public void delete(Integer id) {
        service.delete(id);
    }


    public BrandData get(Integer id) throws ApiException {
        return HelperUtil.convertBrand(service.get(id));
    }


    public List<BrandData> getAll() {
        List<BrandPojo> pojoList = service.getAll();
        List<BrandData> brandDataList = new ArrayList<BrandData>();

        pojoList.forEach(brandPojo -> {
            brandDataList.add(HelperUtil.convertBrand(brandPojo));
        });

        return brandDataList;
    }

    public void update(Integer id, BrandForm form) throws ApiException {
        normalizeBrandForm(form);
        validateBrandForm(form);
        flow.update(id, HelperUtil.convertBrand(form));
    }


    // NORMALISATION AND VALIDATION OF FORM
    protected static void normalizeBrandForm(BrandForm brandForm) {
        brandForm.setBrand(StringUtil.toLowerCase(brandForm.getBrand()));
        brandForm.setCategory(StringUtil.toLowerCase(brandForm.getCategory()));
    }
    protected void validateBrandForm(BrandForm brandForm) throws ApiException {
        if(StringUtil.isEmpty(brandForm.getBrand())) {
            throw new ApiException("Brand cannot be empty");
        }
        if(StringUtil.isEmpty(brandForm.getCategory())) {
            throw new ApiException("Category cannot be empty");
        }
    }


}

