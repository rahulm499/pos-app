package com.increff.pos.flow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.model.data.InvoiceData;
import com.increff.pos.model.data.InvoiceItemData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderApiService;
import com.increff.pos.service.ProductApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class InvoiceFlow {
    @Autowired
    private OrderFlow orderFlow;

    @Autowired
    private OrderApiService orderApiService;
    @Autowired
    private ProductApiService productApiService;
    public InvoiceData generateInvoiceData(Integer id) throws ApiException {
        return convertInvoiceDataList(orderFlow.getOrderItems(id), orderApiService.get(id));
    }

    public String generateInvoice(InvoiceData invoiceData) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:9001/invoice/api/generate-invoice";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = objectMapper.writeValueAsString(invoiceData);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        String response = restTemplate.postForObject(url, request, String.class);
        return response;
    }

    @Transactional(rollbackFor = IOException.class)
    public void storeInvoice(String pdfStream, Integer id) throws IOException {
        String filePath = "src/main/resources/apache\\order_"+id+".pdf";
        byte[] decodedBytes = Base64.getDecoder().decode(pdfStream);
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        fileOutputStream.write(decodedBytes);
        fileOutputStream.close();
    }
    @Transactional(rollbackFor = ApiException.class)
    public void setOrderStatus(Integer id) throws ApiException {
        orderFlow.updateInvoiceStatus(id);
    }

    protected InvoiceData convertInvoiceDataList(List<OrderItemData> orderItemDataList, OrderPojo orderPojo) throws ApiException {
        List<InvoiceItemData> invoiceItemDataList = new ArrayList<>();
        int index=1;
        Double total=0.00;
        for(OrderItemData orderItemData: orderItemDataList){
            InvoiceItemData invoiceItemData=convertInvoiceData(orderItemData, productApiService.getCheckBarcode(orderItemData.getBarcode()).getName(), index);
            total+=invoiceItemData.getAmount();
            invoiceItemDataList.add(invoiceItemData);
        }
        InvoiceData invoiceData = new InvoiceData();
        invoiceData.setInvoiceItemDataList(invoiceItemDataList);
        invoiceData.setOrderId(orderPojo.getId());
        invoiceData.setTotalAmount(total);
        String date = String.valueOf(orderPojo.getCreated_at().withZoneSameInstant(ZoneId.of("UTC") ));
        String time = date.split("T")[1];
        time = time.split("Z")[0];
        date = date.split("T")[0];
        invoiceData.setDate(date);
        invoiceData.setTime(time);
        return invoiceData;
    }
    protected InvoiceItemData convertInvoiceData(OrderItemData orderItemData, String product, Integer index){
        InvoiceItemData invoiceItemData = new InvoiceItemData();
        invoiceItemData.setQuantity(orderItemData.getQuantity());
        invoiceItemData.setBarcode(orderItemData.getBarcode());
        invoiceItemData.setUnitPrice(orderItemData.getSellingPrice());
        invoiceItemData.setProduct(product);
        invoiceItemData.setIndex(index);
        invoiceItemData.setAmount(invoiceItemData.getUnitPrice()*invoiceItemData.getQuantity());
        return invoiceItemData;

    }
}
