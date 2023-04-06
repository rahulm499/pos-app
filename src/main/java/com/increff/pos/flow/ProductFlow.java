package com.increff.pos.flow;

import com.increff.pos.api.InventoryApi;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandApi;
import com.increff.pos.api.ProductApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductFlow {
    @Autowired
    private BrandApi brandApi;
    @Autowired
    private ProductApi productApi;
    @Autowired
    private InventoryApi inventoryApi;

    @Transactional(rollbackFor = ApiException.class)
    public void add(ProductPojo productPojo, String brand, String category) throws ApiException {
        productApi.validateBarcode(productPojo.getBarcode());
        BrandPojo brandPojo = brandApi.getCheckBrandCategory(brand, category);
        productPojo.setBrand_category(brandPojo.getId());
        productApi.add(productPojo);
        InventoryPojo inventoryPojo= new InventoryPojo();
        inventoryPojo.setQuantity(0);
        inventoryPojo.setProductId(productPojo.getId());
        inventoryApi.add(inventoryPojo); // Adding Inventory of product with 0 quantity
    }

    public BrandPojo getBrandByProductId(Integer id) throws ApiException {
        ProductPojo productPojo = productApi.getCheck(id);
        return brandApi.get(productPojo.getBrand_category());
    }

}
