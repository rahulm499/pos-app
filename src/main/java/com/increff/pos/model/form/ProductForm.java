package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {
    private String barcode;
    private String brandCategory;
    private String brandName;
    private String name;
    private Double mrp;

}
