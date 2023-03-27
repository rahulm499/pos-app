package com.increff.pos.flow;

import com.increff.pos.model.data.ProductData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandApi;
import com.increff.pos.service.ProductApi;
import com.increff.pos.helper.ProductHelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ProductFlow {
    @Autowired
    private BrandApi brandApi;
    @Autowired
    private ProductApi productApi;

    @Transactional(rollbackFor = ApiException.class)
    public void add(ProductPojo productPojo, String brand, String category) throws ApiException {
        validate(productPojo);
        BrandPojo brandPojo = brandApi.getCheckBrandCategory(brand, category);
        productPojo.setBrand_category(brandPojo.getId());
        productApi.add(productPojo);
    }
    @Transactional(rollbackFor = ApiException.class)
    public Map<Integer, List<Object>> get(Integer id) throws ApiException {
        Map<Integer, List<Object>> map = new HashMap<>();
        List<Object> values = new ArrayList<>();
        ProductPojo productPojo = productApi.get(id);
        BrandPojo brandPojo = brandApi.get(productPojo.getBrand_category());
        values.add(productPojo.getId());
        values.add(productPojo.getName());
        values.add(productPojo.getBarcode());
        values.add(productPojo.getMrp());
        values.add(brandPojo.getBrand());
        values.add(brandPojo.getCategory());
        map.put(productPojo.getId(), values);
        return map;

    }
    public Integer getProductId(String Barcode) throws ApiException {
        return productApi.getCheckBarcode(Barcode).getId();
    }

    protected void validate(ProductPojo productPojo) throws ApiException{
        productApi.validateBarcode(productPojo.getBarcode());
    }
}
