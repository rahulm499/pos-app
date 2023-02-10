package com.increff.pos.flow;

import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.helper.InventoryHelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class InventoryFlow {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductService productService;

    @Transactional(rollbackOn = ApiException.class)
    public void add(InventoryForm form) throws ApiException {
        ProductPojo productPojo = productService.getCheckBarcode(form.getBarcode());
        InventoryPojo inventoryPojo = InventoryHelperUtil.convertInventory(form, productPojo.getId());
        inventoryService.add(inventoryPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryData get(Integer id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryService.get(id);
        ProductPojo productPojo = productService.get(inventoryPojo.getProductId());
        InventoryData data = InventoryHelperUtil.convertInventory(inventoryPojo);
        data.setBarcode(productPojo.getBarcode());
        return data;
    }
}
