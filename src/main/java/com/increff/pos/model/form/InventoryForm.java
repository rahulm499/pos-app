package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryForm {
    private String barcode;
    private Integer quantity;
}
