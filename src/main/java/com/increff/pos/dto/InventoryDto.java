package com.increff.pos.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryApi;
import com.increff.pos.service.ProductApi;
import com.increff.pos.util.StringUtil;
import com.increff.pos.helper.InventoryHelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.increff.pos.helper.InventoryHelperUtil.*;

@Service
public class InventoryDto {
    @Autowired
    private InventoryApi inventoryApi;
    @Autowired
    private ProductApi productApi;
    @Autowired
    private InventoryFlow inventoryFlow;
    @Autowired
    private ObjectMapper objectMapper;
    @Transactional(rollbackFor = ApiException.class)
    public void add(InventoryForm form) throws ApiException {
        normalize(form);
        validate(form);
        inventoryFlow.add(form.getQuantity(), form.getBarcode());
    }

    @Transactional(rollbackFor = ApiException.class)
    public ResponseEntity<byte[]> addBulk(MultipartFile file) throws ApiException, IOException, IllegalAccessException {
        List<InventoryForm> inventoryFormList = extractFileInventoryData(file);
        List<ErrorData> inventoryErrorDataList = new ArrayList<>();
        int index =1, flag=0;
        for(InventoryForm inventoryForm: inventoryFormList){
            try{add(inventoryForm);}
            catch(ApiException e){
                flag=1;
                ErrorData errorData = createErrorData(inventoryForm, e.getMessage(), index);
                inventoryErrorDataList.add(errorData);
            }
            index++;
        }
        if(flag == 0){
            return null;
        }
        return convertToTSVFile(inventoryErrorDataList);
    }

    public InventoryData get(Integer id) throws ApiException {
        List<Object> inventoryObject = inventoryFlow.get(id);
        InventoryData inventoryData = convertInventory((InventoryPojo) inventoryObject.get(0));
        inventoryData.setBarcode((String) inventoryObject.get(1));
        return inventoryData;
    }
    public List<InventoryData> getAll() throws ApiException {
        List<InventoryPojo> inventoryPojoList = inventoryApi.getAll();
        Map<Integer, String> inventoryFlowMap = inventoryFlow.getAll();
        List<InventoryData> dataList = new ArrayList<>();
        for(InventoryPojo inventoryPojo: inventoryPojoList){
            InventoryData data = InventoryHelperUtil.convertInventory(inventoryPojo);
            data.setBarcode(inventoryFlowMap.get(inventoryPojo.getId()));
            dataList.add(data);
        }
        return dataList;
    }

    @Transactional(rollbackFor = ApiException.class)
    public void update(Integer id, InventoryForm form) throws ApiException {
        normalize(form);
        validate(form);
        inventoryApi.update(id, convertInventory(form.getQuantity(), id));
    }

}
