package com.increff.pos.flow;

import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryApiService;
import com.increff.pos.service.ProductApiService;
import com.increff.pos.util.helper.InventoryHelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryFlow {
    @Autowired
    private InventoryApiService inventoryApiService;
    @Autowired
    private ProductApiService productApiService;

    @Transactional(rollbackFor = ApiException.class)
    public void add(InventoryForm form) throws ApiException {
        ProductPojo productPojo = productApiService.getCheckBarcode(form.getBarcode());
        InventoryPojo inventoryPojo = InventoryHelperUtil.convertInventory(form, productPojo.getId());
        inventoryApiService.add(inventoryPojo);
    }

    @Transactional(rollbackFor = ApiException.class)
    public InventoryData get(Integer id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryApiService.get(id);
        ProductPojo productPojo = productApiService.get(inventoryPojo.getProductId());
        InventoryData data = InventoryHelperUtil.convertInventory(inventoryPojo);
        data.setBarcode(productPojo.getBarcode());
        return data;
    }
}
