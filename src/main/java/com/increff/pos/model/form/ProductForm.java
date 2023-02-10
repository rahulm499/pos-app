package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {
    private String barcode;
    private String brand_category;
    private String brand_name;
    private String name;
    private Double mrp;

}
