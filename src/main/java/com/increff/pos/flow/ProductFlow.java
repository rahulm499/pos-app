package com.increff.pos.flow;

import com.increff.pos.model.data.ProductData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandApiService;
import com.increff.pos.service.ProductApiService;
import com.increff.pos.util.helper.ProductHelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductFlow {
    @Autowired
    private BrandApiService brandApiService;
    @Autowired
    private ProductApiService productApiService;

    @Transactional(rollbackFor = ApiException.class)
    public void add(ProductPojo productPojo, String brand, String category) throws ApiException {
        validate(productPojo);
        BrandPojo brandPojo = brandApiService.getCheckBrandCategory(brand, category);
        productPojo.setBrand_category(brandPojo.getId());
        productApiService.add(productPojo);
    }
    @Transactional(rollbackFor = ApiException.class)
    public ProductData get(Integer id) throws ApiException {
        ProductPojo productPojo = productApiService.get(id);
        BrandPojo brandPojo = brandApiService.get(productPojo.getBrand_category());
        ProductData data =  ProductHelperUtil.convertProduct(productPojo, brandPojo.getBrand(), brandPojo.getCategory());
        return data;

    }

    protected void validate(ProductPojo productPojo) throws ApiException{
        productApiService.validateBarcode(productPojo.getBarcode());
    }
}
