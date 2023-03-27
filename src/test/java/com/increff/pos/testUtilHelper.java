package com.increff.pos;

import com.increff.pos.model.form.*;

import java.util.ArrayList;
import java.util.List;

public class testUtilHelper {

    public static BrandForm getDummyBrandForm(String brand, String category){
        BrandForm brandForm = new BrandForm();
        brandForm.setBrand(brand);
        brandForm.setCategory(category);
        return brandForm;
    }

    public static ProductForm getDummyProductForm(String barcode, String brand, String category, String name, Double mrp){
        ProductForm form = new ProductForm();
        form.setBarcode(barcode);
        form.setName(name);
        form.setMrp(mrp);
        form.setBrandName(brand);
        form.setBrandCategory(category);
        return form;
    }

    public static InventoryForm getDummyInventoryForm(String barcode, Integer quantity){
        InventoryForm form = new InventoryForm();
        form.setBarcode(barcode);
        form.setQuantity(quantity);
        return form;
    }

    public static OrderItemForm getOrderItemForm(String barcode, Integer quantity, Double sellingPrice){
        OrderItemForm form = new OrderItemForm();
        form.setBarcode(barcode);
        form.setSellingPrice(sellingPrice);
        form.setQuantity(quantity);
        return form;
    }
    public static OrderForm getDummyOrderFrom(List<OrderItemForm> orderItemFormList){
        OrderForm form = new OrderForm();
        form.setOrderItems(orderItemFormList);
        return form;
    }

}
