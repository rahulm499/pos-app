package com.increff.pos.dto;

import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.api.ApiException;
import com.increff.pos.api.ProductApi;
import com.increff.pos.helper.ProductHelperUtil;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.increff.pos.helper.ProductHelperUtil.*;

@Service
public class ProductDto {
    @Autowired
    private ProductApi productApi;
    @Autowired
    private ProductFlow productFlow;

    public void add(ProductForm form) throws ApiException {
        ProductHelperUtil.productFormNormalize(form);
        ProductHelperUtil.productFormValidation(form);
        productFlow.add(ProductHelperUtil.convertProduct(form), form.getBrandName(), form.getBrandCategory());
    }

    public void update(Integer id, ProductForm form) throws ApiException {
        ProductHelperUtil.updateProductFormNormalize(form);
        ProductHelperUtil.updateProductFormValidation(form);
        ProductPojo p = ProductHelperUtil.convertProduct(form);
        productApi.update(id, p);
    }

    @Transactional
    public ByteArrayOutputStream addBulk(MultipartFile file) throws ApiException {
        List<ProductForm> productFormList = ProductHelperUtil.extractFileProductData(file);
        List<ErrorData> productErrorDataList = new ArrayList<>();
        boolean errorOccurred = false;
        for (int i = 0; i < productFormList.size(); i++) {
            try {
                add(productFormList.get(i));
            } catch (ApiException e) {
                errorOccurred = true;
                ErrorData errorData = createErrorData(productFormList.get(i), e.getMessage(), i + 1);
                productErrorDataList.add(errorData);
            }
        }
        if (!errorOccurred) {
            return null;
        }
        return ProductHelperUtil.convertToTSVFile(productErrorDataList);
    }


    public ProductData get(Integer id) throws ApiException {
        ProductPojo productPojo = productApi.getCheck(id);
        BrandPojo brandPojo = productFlow.getBrandByProductId(id);
        return ProductHelperUtil.convertProduct(productPojo, brandPojo);
    }

    public List<ProductData> getAll() throws ApiException {
        List<ProductPojo> productPojoList = productApi.getAll();
        List<ProductData> productDataList = new ArrayList<ProductData>();
        for (ProductPojo productPojo : productPojoList) {
            BrandPojo brandPojo = productFlow.getBrandByProductId(productPojo.getId());
            productDataList.add(ProductHelperUtil.convertProduct(productPojo, brandPojo));
        }
        return productDataList;
    }


}
