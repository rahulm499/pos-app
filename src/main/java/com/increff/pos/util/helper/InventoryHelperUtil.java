package com.increff.pos.util.helper;

import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;

public class InventoryHelperUtil {
    public static InventoryData convertInventory(InventoryPojo p) {
        InventoryData d =new InventoryData();
        d.setId(p.getId());
        d.setQuantity(p.getQuantity());
        return d;
    }

    // BrandData -> BrandPojo
    public static InventoryPojo convertInventory(InventoryForm f, Integer id) {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setQuantity(f.getQuantity());
        inventoryPojo.setProductId(id);
        return inventoryPojo;
    }
}
