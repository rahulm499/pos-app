package com.increff.pos.client;

import com.increff.pos.model.data.InvoiceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InvoiceClient {
    @Value("${invoice.url}")
    private String url;

    @Autowired
    private RestTemplate restTemplate;


    public String generateInvoice(InvoiceData invoiceData){
        HttpEntity<InvoiceData> requestEntity = new HttpEntity<>(invoiceData, createJSONHeaders());
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        return responseEntity.getBody();
    }

    private HttpHeaders createJSONHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
