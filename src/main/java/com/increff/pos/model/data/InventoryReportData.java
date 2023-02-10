package com.increff.pos.model.data;

import com.increff.pos.model.form.BrandForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryReportData extends BrandForm {
    private Integer quantity;
}
