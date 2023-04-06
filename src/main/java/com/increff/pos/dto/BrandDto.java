package com.increff.pos.dto;


import com.increff.pos.helper.BrandHelperUtil;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandApi;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.increff.pos.helper.BrandHelperUtil.*;

@Service
public class BrandDto {
    @Autowired
    private BrandApi brandApi;

    public void add(BrandForm form) throws ApiException {
        normalizeBrandForm(form);
        validateBrandForm(form);
        brandApi.add(convertBrandForm(form));
    }

    public void update(Integer id, BrandForm form) throws ApiException {
        normalizeBrandForm(form);
        validateBrandForm(form);
        brandApi.update(id, convertBrandForm(form));
    }

    @Transactional
    public ByteArrayOutputStream addBulk(MultipartFile file) throws ApiException {
        List<BrandForm> brandFormList = BrandHelperUtil.extractFileBrandData(file);
        List<ErrorData> brandErrorData = new ArrayList<>();
        boolean errorOccurred = false;
        for (int i = 0; i < brandFormList.size(); i++) {
            try {
                add(brandFormList.get(i));
            } catch (ApiException e) {
                errorOccurred = true;
                ErrorData errorData = createErrorData(brandFormList.get(i), e.getMessage(), i + 1);
                brandErrorData.add(errorData);
            }
        }
        if (!errorOccurred) {
            return null;
        }
        return BrandHelperUtil.convertToTSVFile(brandErrorData);
    }

    public BrandData get(Integer id) throws ApiException {
        return convertBrand(brandApi.getCheck(id));
    }


    public List<BrandData> getAll() {
        List<BrandPojo> pojoList = brandApi.getAll();
        List<BrandData> brandDataList = new ArrayList<BrandData>();

        pojoList.forEach(brandPojo -> {
            brandDataList.add(convertBrand(brandPojo));
        });

        return brandDataList;
    }


}

