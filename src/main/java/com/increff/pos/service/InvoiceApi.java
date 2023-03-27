package com.increff.pos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.pos.client.InvoiceClient;
import com.increff.pos.model.data.InvoiceData;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;


@Setter
@Service
public class InvoiceApi {
    @Autowired
    private InvoiceClient invoiceClient;

    //validation checks need to be updated
    @Transactional(rollbackOn = ApiException.class)
    public String generateInvoice(InvoiceData invoiceData) throws ApiException, JsonProcessingException {
        return invoiceClient.generateInvoice(invoiceData);
    }



}
