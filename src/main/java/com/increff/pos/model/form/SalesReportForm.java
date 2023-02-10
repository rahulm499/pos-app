package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesReportForm extends BrandForm{
    private String startDate;
    private String endDate;
}
