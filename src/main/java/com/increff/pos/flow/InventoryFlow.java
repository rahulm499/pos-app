package com.increff.pos.flow;

import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryApi;
import com.increff.pos.service.ProductApi;
import com.increff.pos.helper.InventoryHelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventoryFlow {
    @Autowired
    private InventoryApi inventoryApi;
    @Autowired
    private ProductApi productApi;

    @Transactional(rollbackFor = ApiException.class)
    public void add(Integer quantity, String barcode) throws ApiException {
        ProductPojo productPojo = productApi.getCheckBarcode(barcode);
        InventoryPojo inventory = inventoryApi.getByProduct(productPojo.getId());
        if(inventory!=null){
            InventoryPojo inventoryPojo = new InventoryPojo();
            inventoryPojo.setProductId(inventory.getProductId());
            inventoryPojo.setQuantity(inventory.getQuantity() + quantity);
            inventoryApi.update(inventory.getId(), inventoryPojo);
        }else{
            InventoryPojo inventoryPojo = InventoryHelperUtil.convertInventory(quantity, productPojo.getId());
            inventoryApi.add(inventoryPojo);
        }

    }

    @Transactional(rollbackFor = ApiException.class)
    public List<Object> get(Integer id) throws ApiException {
        List<Object> inventoryObject = new ArrayList<>();
        InventoryPojo inventoryPojo = inventoryApi.get(id);
        ProductPojo productPojo = productApi.get(inventoryPojo.getProductId());
        inventoryObject.add(inventoryPojo);
        inventoryObject.add(productPojo.getBarcode());
        return inventoryObject;
    }
    @Transactional(rollbackFor = ApiException.class)
    public Map<Integer, String> getAll() throws ApiException {
        Map<Integer, String> map = new HashMap<>();
        List<InventoryPojo> inventoryPojoList = inventoryApi.getAll();
        for(InventoryPojo inventoryPojo: inventoryPojoList){
            ProductPojo productPojo = productApi.get(inventoryPojo.getProductId());
            map.put(inventoryPojo.getId(), productPojo.getBarcode());
        }
        return map;
    }
}
