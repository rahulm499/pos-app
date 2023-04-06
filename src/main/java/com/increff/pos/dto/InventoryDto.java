package com.increff.pos.dto;

import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.api.ApiException;
import com.increff.pos.api.InventoryApi;
import com.increff.pos.helper.InventoryHelperUtil;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.increff.pos.helper.InventoryHelperUtil.*;

@Service
public class InventoryDto {
    @Autowired
    private InventoryApi inventoryApi;
    @Autowired
    private InventoryFlow inventoryFlow;

    public void add(InventoryForm form) throws ApiException {
        normalize(form);
        validate(form);
        inventoryFlow.add(form.getQuantity(), form.getBarcode());
    }

    public void update(Integer id, InventoryForm form) throws ApiException {
        normalize(form);
        validate(form);
        inventoryApi.update(id, convertInventory(form.getQuantity(), id));
    }

    @Transactional // Transactional
    public ByteArrayOutputStream addBulk(MultipartFile file) throws ApiException {
        List<InventoryForm> inventoryFormList = extractFileInventoryData(file);
        List<ErrorData> inventoryErrorDataList = new ArrayList<>();
        boolean errorOccurred = false;
        for (int i = 0; i < inventoryFormList.size(); i++) {
            try {
                add(inventoryFormList.get(i));
            } catch (ApiException e) {
                errorOccurred = true;
                ErrorData errorData = createErrorData(inventoryFormList.get(i), e.getMessage(), i + 1);
                inventoryErrorDataList.add(errorData);
            }
        }
        if (!errorOccurred) {
            return null;
        }
        return convertToTSVFile(inventoryErrorDataList);
    }

    public InventoryData get(Integer id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryApi.getCheck(id);
        String barcode = inventoryFlow.getInventoryBarcode(id);
        InventoryData inventoryData = convertInventory(inventoryPojo, barcode);
        return inventoryData;
    }

    public List<InventoryData> getAll(){
        List<InventoryPojo> inventoryPojoList = inventoryApi.getAll();
        Map<Integer, String> inventoryFlowMap = inventoryFlow.getAll();
        List<InventoryData> dataList = new ArrayList<>();
        for (InventoryPojo inventoryPojo : inventoryPojoList) {
            InventoryData data = InventoryHelperUtil.convertInventory(inventoryPojo, inventoryFlowMap.get(inventoryPojo.getId()));
            dataList.add(data);
        }
        return dataList;
    }


}
