package com.increff.pos.model.data;

import com.increff.pos.model.form.InvoiceForm;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InvoiceData extends InvoiceForm {
    private String date;
    private String time;
    private List<InvoiceItemData> invoiceItemDataList;
    private Double totalAmount;

}
