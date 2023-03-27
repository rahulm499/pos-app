package com.increff.pos.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.util.StringUtil;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class InventoryHelperUtil {
    @Autowired
    private static ObjectMapper mapper;
    public static List<InventoryForm> extractFileInventoryData(MultipartFile file) throws ApiException {
        List<InventoryForm> inventoryFormList = new ArrayList<>();
        int index=0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                InventoryForm inventory = new InventoryForm();
                if(index ==0){
                    vaidateHeadings(data);
                    index++;
                    continue;
                }
                inventory.setBarcode(data[0]);
                inventory.setQuantity(Integer.valueOf(data[1]));
                inventoryFormList.add(inventory);
                index++;
            }

        } catch (IOException |ApiException e) {
            throw new ApiException(e.getMessage());
        }
        return inventoryFormList;
    }

    public static ResponseEntity<byte[]> convertToTSVFile(List<ErrorData> inventoryErrorDataList) throws IllegalAccessException, IOException {
        StringBuilder tsvData = new StringBuilder();

        // Append the header row (if any)
        // Assumes that the objects in the list have properties that correspond to the column names
        tsvData.append("#\tbarcode\tquantity\tmessage\n");

        // Loop through the list of objects and append the data to the StringBuilder
        for (ErrorData errorData : inventoryErrorDataList) {
            // Assumes that the objects in the list have getters that correspond to the column names
            tsvData.append(errorData.getId()).append("\t")
                    .append(errorData.getValues().get(0)).append("\t")
                    .append(errorData.getValues().get(1)).append("\t")
                    .append(errorData.getMessage()).append("\n");

        }
        // create a byte array output stream and write the TSV data to it
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(tsvData.toString().getBytes());

        // create headers for the response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("filename", "data.tsv");

        // Create response entity
        ResponseEntity<byte[]> response = new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);

        return response;

    }


    public static void normalize(InventoryForm form) {
        form.setBarcode(StringUtil.toLowerCase(form.getBarcode()));
    }

    public static void validate(InventoryForm form) throws ApiException {
        if(StringUtil.isEmpty(form.getBarcode())){
            throw new ApiException("Barcode cannot be empty");
        }
        if(form.getQuantity()==null){
            throw new ApiException("Quantity cannot be empty");
        }
        if(form.getQuantity()<0){
            throw new ApiException("Quantity cannot be less than 0");
        }
    }

    public static ErrorData createErrorData(InventoryForm inventoryForm, String message, Integer index){
        ErrorData errorData = new ErrorData();
        errorData.setId(index);
        errorData.setMessage(message);
        List<Object> values = new ArrayList<>();
        values.add(inventoryForm.getBarcode());
        values.add(inventoryForm.getQuantity());
        errorData.setValues(values);
        return errorData;
    }
    public static InventoryData convertInventory(InventoryPojo p) {
        InventoryData d =new InventoryData();
        d.setId(p.getId());
        d.setQuantity(p.getQuantity());
        return d;
    }

    // BrandData -> BrandPojo
    public static InventoryPojo convertInventory(Integer quantity, Integer id) {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setQuantity(quantity);
        inventoryPojo.setProductId(id);
        return inventoryPojo;
    }
    protected static void vaidateHeadings(String[] data) throws ApiException {
        if(!data[0].equals("barcode") || !data[1].equals("quantity")){
            throw new ApiException("Invalid data headings");
        }
    }
}
