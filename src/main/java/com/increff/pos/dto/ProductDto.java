package com.increff.pos.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.ProductApi;
import com.increff.pos.helper.ProductHelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.increff.pos.helper.ProductHelperUtil.*;

@Service
public class ProductDto {
    @Autowired
    private ProductApi productApi;
    @Autowired
    private ProductFlow productFlow;

    @Transactional(rollbackFor = ApiException.class)
    public void add(ProductForm form) throws ApiException {
        ProductHelperUtil.productFormNormalize(form);
        ProductHelperUtil.productFormValidation(form);
        productFlow.add(ProductHelperUtil.convertProduct(form), form.getBrandName(), form.getBrandCategory());
    }
    @Transactional(rollbackFor = ApiException.class)
    public ResponseEntity<byte[]> addBulk(MultipartFile file) throws ApiException, IOException, IllegalAccessException {
        List<ProductForm> productFormList = ProductHelperUtil.extractFileProductData(file);
        List<ErrorData> productErrorDataList = new ArrayList<>();
        int index =1, flag= 0;
        for(ProductForm productForm: productFormList){
            try{add(productForm);}
            catch(ApiException e){
                flag=1;
                ErrorData errorData = createErrorData(productForm, e.getMessage(), index);
                productErrorDataList.add(errorData);
            }
            index++;
        }
        if(flag == 0){
            return null;
        }
        return ProductHelperUtil.convertToTSVFile(productErrorDataList);
    }


    public ProductData get(Integer id) throws ApiException {
        Map<Integer, List<Object>> map = productFlow.get(id);
        ProductData productData = ProductHelperUtil.convertProduct(map.get(id));
        return productData;
    }
    public List<ProductData> getAll() throws ApiException {
        List<ProductPojo> productPojoList = productApi.getAll();
        List<ProductData> productDataList = new ArrayList<ProductData>();
        for(ProductPojo productPojo: productPojoList) {
            productDataList.add(get(productPojo.getId()));
        }
        return productDataList;
    }

    @Transactional(rollbackFor = ApiException.class)
    public void update(Integer id, ProductForm form) throws ApiException {
        ProductHelperUtil.updateProductFormNormalize(form);
        ProductHelperUtil.updateProductFormValidation(form);
        ProductPojo p = ProductHelperUtil.convertProduct(form);
        productApi.update(id, p);
    }

}
