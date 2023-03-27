package com.increff.pos.dto;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.helper.BrandHelperUtil;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.increff.pos.helper.BrandHelperUtil.*;

@Service
public class BrandDto {
    @Autowired
    private BrandApi brandApi;
    @Autowired
    private ObjectMapper objectMapper;
    @Transactional(rollbackFor = ApiException.class)
    public void add(BrandForm form) throws ApiException {
        normalizeBrandForm(form);
        validateBrandForm(form);
        brandApi.add(convertBrand(form));
    }

    @Transactional(rollbackFor = ApiException.class)
    public ResponseEntity<byte[]> addBulk(MultipartFile file) throws ApiException, IOException, IllegalAccessException {
        List<BrandForm> brandFormList = BrandHelperUtil.extractFileBrandData(file);
        List<ErrorData> brandErrorData = new ArrayList<>();
        int index =1, flag=0;
        for(BrandForm brandForm: brandFormList){
            try{add(brandForm);}
            catch(ApiException e){
                flag=1;
                ErrorData errorData = createErrorData(brandForm, e.getMessage(), index);
                brandErrorData.add(errorData);
            }
            index++;
        }
        if(flag == 0){
            return null;
        }
        return BrandHelperUtil.convertToTSVFile(brandErrorData);
    }

    public BrandData get(Integer id) throws ApiException {
        return convertBrand(brandApi.get(id));
    }


    public List<BrandData> getAll() {
        List<BrandPojo> pojoList = brandApi.getAll();
        List<BrandData> brandDataList = new ArrayList<BrandData>();

        pojoList.forEach(brandPojo -> {
            brandDataList.add(convertBrand(brandPojo));
        });

        return brandDataList;
    }

    @Transactional(rollbackFor = ApiException.class)
    public void update(Integer id, BrandForm form) throws ApiException {
        normalizeBrandForm(form);
        validateBrandForm(form);
        brandApi.update(id, convertBrand(form));
    }




}

