package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceItemData {
    private Integer index;
    private String barcode;
    private String product;
    private Integer quantity;
    private Double unitPrice;
    private Double amount;

}
