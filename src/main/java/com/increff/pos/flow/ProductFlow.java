package com.increff.pos.flow;

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
import javax.transaction.Transactional;


@Component
public class ProductFlow {
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductService productService;

    @Transactional(rollbackOn = ApiException.class)
    public void add(ProductPojo productPojo, String brand, String category) throws ApiException {
        validate(productPojo);
        BrandPojo brandPojo = brandService.getCheckBrandCategory(brand, category);
        if(brandPojo == null){
            throw new ApiException("Brand Category Pair does not exists");
        }
        productPojo.setBrand_category(brandPojo.getId());
        productService.add(productPojo);
    }
    @Transactional(rollbackOn = ApiException.class)
    public ProductData get(Integer id) throws ApiException {
        ProductPojo productPojo = productService.get(id);
        BrandPojo brandPojo = brandService.get(productPojo.getBrand_category());
        ProductData data =  ProductHelperUtil.convertProduct(productPojo, brandPojo.getBrand(), brandPojo.getCategory());
        return data;

    }

    protected void validate(ProductPojo productPojo) throws ApiException{
        productService.validateBarcode(productPojo.getBarcode());
    }
}
