package com.increff.pos.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.flow.InvoiceFlow;
import com.increff.pos.model.data.InvoiceData;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;


@Component
public class InvoiceDto {

    @Autowired
    private InvoiceFlow invoiceFlow;

    public String generatePdf(Integer id) throws Exception {
        InvoiceData invoiceData = invoiceFlow.generateInvoiceData(id);
        String pdfStream = invoiceFlow.generateInvoice(invoiceData);
        invoiceFlow.storeInvoice(pdfStream, id);
        invoiceFlow.setOrderStatus(id);
        return pdfStream;
    }

    public ResponseEntity<byte[]> downloadPdf(Integer id) throws Exception {
        Path pdf = Paths.get("./src/main/resources/apache/order_" + id + ".pdf");
        byte[] contents = Files.readAllBytes(pdf);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "order_" + id + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return response;
    }
}
