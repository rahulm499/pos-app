package com.increff.pos.util.helper;

import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.util.StringUtil;

public class ProductHelperUtil {
    public static ProductData convertProduct(ProductPojo p, String brand, String category) {
        ProductData data =new ProductData();
        data.setId(p.getId());
        data.setBarcode(p.getBarcode());
        data.setName(p.getName());
        data.setMrp(p.getMrp());
        data.setBrand_name(brand);
        data.setBrand_category(category);
        return data;
    }

    // BrandData -> BrandPojo
    public static ProductPojo convertProduct(ProductForm f) {
        ProductPojo p = new ProductPojo();
        p.setBarcode(f.getBarcode());
        p.setName(f.getName());
        p.setMrp(f.getMrp());
        return p;
    }

    public static void productFormValidation(ProductForm form) throws ApiException {
        if(StringUtil.isEmpty(form.getBarcode())){
            throw new ApiException("Barcode cannot be empty");
        }
        if(StringUtil.isEmpty(form.getBrand_name())){
            throw new ApiException("Brand Name cannot be empty");
        }
        if(StringUtil.isEmpty(form.getBrand_category())){
            throw new ApiException("Category cannot be empty");
        }
        if(StringUtil.isEmpty(form.getName())){
            throw new ApiException("Product Name cannot be empty");
        }
        if(form.getMrp() == null){
            throw new ApiException("Invalid MRP");
        }
        if(form.getMrp()<=0){
            throw new ApiException("MRP must be positive");
        }

    }
    public static ProductForm productFormNormalize(ProductForm form){
        form.setBarcode(StringUtil.toLowerCase(form.getBarcode()));
        form.setName(StringUtil.toLowerCase(form.getName()));
        form.setBrand_category(StringUtil.toLowerCase(form.getBrand_category()));
        form.setBrand_name(StringUtil.toLowerCase(form.getBrand_name()));
        return form;
    }
}
