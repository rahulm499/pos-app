package com.increff.pos.dto;

import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.ProductApiService;
import com.increff.pos.util.helper.ProductHelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductDto {
    @Autowired
    private ProductApiService productApiService;
    @Autowired
    private ProductFlow flow;

    public void add(ProductForm form) throws ApiException {
        ProductHelperUtil.productFormNormalize(form);
        ProductHelperUtil.productFormValidation(form);
        flow.add(ProductHelperUtil.convertProduct(form), form.getBrand_name(), form.getBrand_category());
    }

    public void delete(Integer id) {
        productApiService.delete(id);
    }

    public ProductData get(Integer id) throws ApiException {
        return flow.get(id);
    }
    public List<ProductData> getAll() throws ApiException {
        List<ProductPojo> productPojoList = productApiService.getAll();
        List<ProductData> productDataList = new ArrayList<ProductData>();
        for(ProductPojo productPojo: productPojoList) {
            productDataList.add(get(productPojo.getId()));
        }
        return productDataList;
    }

    public void update(Integer id, ProductForm form) throws ApiException {
        ProductHelperUtil.productFormNormalize(form);
        ProductHelperUtil.productFormValidation(form);
        ProductPojo p = ProductHelperUtil.convertProduct(form);
        productApiService.update(id, p);
    }
}
