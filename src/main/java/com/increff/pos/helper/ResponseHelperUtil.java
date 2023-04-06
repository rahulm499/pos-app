package com.increff.pos.helper;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ResponseHelperUtil {

    public static ResponseEntity<byte[]> setAddBulkHeaders(ByteArrayOutputStream outputStream){
        if(outputStream == null){
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("filename", "data.tsv");
        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }


}
