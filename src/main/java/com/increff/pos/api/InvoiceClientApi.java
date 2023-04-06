package com.increff.pos.api;

import com.increff.pos.client.InvoiceClient;
import com.increff.pos.model.data.InvoiceData;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;


@Setter
@Service
public class InvoiceClientApi {
    @Autowired
    private InvoiceClient invoiceClient;

    @Transactional(rollbackOn = ApiException.class)
    public String generateInvoice(InvoiceData invoiceData) throws ApiException {
        try {
            return invoiceClient.generateInvoice(invoiceData);
        }catch (Exception e){
            throw new ApiException("Unable to generate invoice");
        }
    }



}
