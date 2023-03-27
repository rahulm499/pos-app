package com.increff.pos.dto;

import com.increff.pos.client.InvoiceClient;
import com.increff.pos.flow.OrderFlow;
import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.InvoiceData;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import com.increff.pos.util.StringUtil;
import com.increff.pos.helper.OrderHelperUtil;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

import static com.increff.pos.helper.OrderHelperUtil.*;

@Service
public class OrderDto {

    @Autowired
    private OrderApi orderApi;
    @Autowired
    private OrderItemApi orderItemApi;

    @Autowired
    private ProductApi productApi;
    @Autowired
    private OrderFlow orderFlow;
    @Autowired
    private ProductFlow productFlow;
    @Autowired
    private InvoiceClient invoiceClient;
    @Autowired
    @Setter
    private InvoiceApi invoiceApi;



    @Transactional(rollbackFor = ApiException.class)
    public void add(OrderForm form) throws ApiException {
        validateOrderForm(form);
        normalizeOrder(form);
        Map<Integer, OrderItemPojo> orderItemPojoList = new HashMap<>();
        Map<Integer, String> orderItemBarcode = new HashMap<>();
        int index =0;
        for(OrderItemForm orderItemForm: form.getOrderItems()){
            orderItemPojoList.put(index, OrderHelperUtil.convertOrderItem(orderItemForm));
            orderItemBarcode.put(index, orderItemForm.getBarcode());
            index++;
        }
        OrderPojo orderPojo = OrderHelperUtil.convertOrder(form);
        orderFlow.add(orderItemPojoList, orderPojo, orderItemBarcode);
    }
    @Transactional(rollbackFor = ApiException.class)
    public void update(OrderForm form, Integer id) throws ApiException {
        validateOrderForm(form);
        normalizeOrder(form);
        Map<Integer, OrderItemPojo> orderItemPojoList = new HashMap<>();
        Map<Integer, String> orderItemBarcode = new HashMap<>();
        int index =0;
        for(OrderItemForm orderItemForm: form.getOrderItems()){
            orderItemPojoList.put(index, OrderHelperUtil.convertOrderItem(orderItemForm));
            orderItemBarcode.put(index, orderItemForm.getBarcode());
            index++;
        }
        orderFlow.update(orderItemPojoList, id, orderItemBarcode);
    }


    @Transactional(rollbackFor = ApiException.class)
    public OrderData get(Integer id) throws ApiException {
        Map<Integer, String> orderItemsBarcode =orderFlow.getOrderItemsBarcode(id);
        List<OrderItemPojo> orderItemPojoList = orderItemApi.getByOrderId(id);
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for(OrderItemPojo orderItemPojo: orderItemPojoList){
            OrderItemData orderItemData = OrderHelperUtil.convertOrderItem(orderItemPojo);
            orderItemData.setBarcode(orderItemsBarcode.get(orderItemPojo.getId()));
            orderItemDataList.add(orderItemData);
        }
        return OrderHelperUtil.convertOrderForm(orderItemDataList, orderApi.get(id));
    }
    @Transactional(rollbackFor = ApiException.class)
    public List<OrderData> getAll() throws ApiException {
        List<OrderPojo> orderPojoList = orderApi.getAll();
        List<OrderData> data = new ArrayList<>();
        for(OrderPojo orderPojo: orderPojoList){
            OrderData d = get(orderPojo.getId());
            data.add(d);
        }
        return data;
    }

    @Transactional(rollbackFor = Exception.class)
    public String generatePdf(Integer id) throws Exception {
        InvoiceData invoiceData = orderFlow.generateInvoiceData(id);
        try{  String pdfStream = invoiceApi.generateInvoice(invoiceData);
            orderFlow.storeInvoice(pdfStream, id);
            orderFlow.updateInvoiceStatus(id);
            return pdfStream;
        }catch(Exception e){
            throw new ApiException("Unable to generate invoice");
        }
    }

    public ResponseEntity<byte[]> downloadPdf(Integer id) throws Exception {
        Path pdf = Paths.get("../.pdfFile/order_" + id + ".pdf");
        byte[] contents = Files.readAllBytes(pdf);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "order_" + id + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return response;
    }

    public void getCheckItem(OrderItemForm form, Integer id) throws ApiException {
        validateOrderItemForm(form);
        normalizeOrderItem(form);
        OrderItemPojo orderItemPojo = OrderHelperUtil.convertOrderItem(form);
        orderFlow.validateOrderItem(orderItemPojo, id, form.getBarcode());
    }



}
