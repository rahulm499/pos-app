package com.increff.pos.dto;

import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.helper.ProductHelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDto {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductFlow flow;

    public void add(ProductForm form) throws ApiException {
        ProductHelperUtil.productFormNormalize(form);
        ProductHelperUtil.productFormValidation(form);
        flow.add(ProductHelperUtil.convertProduct(form), form.getBrand_name(), form.getBrand_category());
    }

    public void delete(Integer id) {
        productService.delete(id);
    }

    public ProductData get(Integer id) throws ApiException {
        return flow.get(id);
    }
    public List<ProductData> getAll() throws ApiException {
        List<ProductPojo> productPojoList = productService.getAll();
        List<ProductData> productDataList = new ArrayList<ProductData>();

        productPojoList.forEach(productPojo -> {
            try {
                productDataList.add(get(productPojo.getId()));
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
        return productDataList;
    }

    public void update(Integer id, ProductForm form) throws ApiException {
        ProductPojo p = ProductHelperUtil.convertProduct(form);
        productService.update(id, p);
    }
}
