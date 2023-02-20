package com.increff.pos.dto;

import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryApiService;
import com.increff.pos.service.ProductApiService;
import com.increff.pos.util.StringUtil;
import com.increff.pos.util.helper.InventoryHelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryDto {
    @Autowired
    private InventoryApiService inventoryApiService;
    @Autowired
    private ProductApiService productApiService;
    @Autowired
    private InventoryFlow inventoryFlow;

    public void add(InventoryForm form) throws ApiException {
        normalize(form);
        validate(form);
        inventoryFlow.add(form);
    }

    public void delete(Integer id) {
        inventoryApiService.delete(id);
    }

    public InventoryData get(Integer id) throws ApiException {
        return inventoryFlow.get(id);
    }
    public List<InventoryData> getAll() throws ApiException {
        List<InventoryPojo> pojoList = inventoryApiService.getAll();
        List<InventoryData> dataList = new ArrayList<InventoryData>();
        for(InventoryPojo inventoryPojo: pojoList){
            dataList.add(get(inventoryPojo.getId()));
        }
        return dataList;
    }

    public void update(Integer id, InventoryForm form) throws ApiException {
        normalize(form);
        validate(form);
        inventoryApiService.update(id, InventoryHelperUtil.convertInventory(form, id));
    }

    protected void normalize(InventoryForm form) {
        form.setBarcode(StringUtil.toLowerCase(form.getBarcode()));
    }

    protected void validate(InventoryForm form) throws ApiException {
        if(StringUtil.isEmpty(form.getBarcode())){
            throw new ApiException("Barcode cannot be empty");
        }
        if(form.getQuantity()==null){
            throw new ApiException("Quantity cannot be empty");
        }
        if(form.getQuantity()<0){
            throw new ApiException("Quantity cannot be less than 0");
        }

    }
}
