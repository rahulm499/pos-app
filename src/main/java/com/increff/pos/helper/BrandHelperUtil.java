package com.increff.pos.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.util.StringUtil;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BrandHelperUtil {
    @Autowired
    private static ObjectMapper mapper;
    public static List<BrandForm> extractFileBrandData(MultipartFile file) throws ApiException {
        List<BrandForm> brandFormList = new ArrayList<>();
        int index=0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                BrandForm brand = new BrandForm();
                if(index ==0){
                   vaidateHeadings(data);
                    index++;
                    continue;
                }
                brand.setBrand(data[0]);
                brand.setCategory(data[1]);
                brandFormList.add(brand);
                index++;
            }

        } catch (IOException | ApiException e) {
           throw new ApiException(e.getMessage());
        }
        return brandFormList;
    }

    public static ResponseEntity<byte[]> convertToTSVFile(List<ErrorData> brandErrorDataList) throws IllegalAccessException, IOException {
        StringBuilder tsvData = new StringBuilder();

        // Append the header row (if any)
        // Assumes that the objects in the list have properties that correspond to the column names
        tsvData.append("#\tbrand\tcategory\tmessage\n");

        // Loop through the list of objects and append the data to the StringBuilder
        for (ErrorData errorData : brandErrorDataList) {
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
    public static BrandData convertBrand(BrandPojo p) {
        BrandData d = new BrandData();
        d.setCategory(p.getCategory());
        d.setBrand(p.getBrand());
        d.setId(p.getId());
        return d;
    }

    // BrandData -> BrandPojo
    public static BrandPojo convertBrand(BrandForm f) {
        BrandPojo p = new BrandPojo();
        p.setCategory(f.getCategory());
        p.setBrand(f.getBrand());
        return p;
    }


    public static void normalizeBrandForm(BrandForm brandForm) {
        brandForm.setBrand(StringUtil.toLowerCase(brandForm.getBrand()));
        brandForm.setCategory(StringUtil.toLowerCase(brandForm.getCategory()));
    }
    public static void validateBrandForm(BrandForm brandForm) throws ApiException {
        if(StringUtil.isEmpty(brandForm.getBrand())) {
            throw new ApiException("Brand cannot be empty");
        }
        if(StringUtil.isEmpty(brandForm.getCategory())) {
            throw new ApiException("Category cannot be empty");
        }
    }

    public static ErrorData createErrorData(BrandForm brandForm, String message, Integer index){
        ErrorData errorData = new ErrorData();
        errorData.setId(index);
        errorData.setMessage(message);
        List<Object> values = new ArrayList<>();
        values.add(brandForm.getBrand());
        values.add(brandForm.getCategory());
        errorData.setValues(values);
        return errorData;
    }
    protected static void vaidateHeadings(String[] data) throws ApiException {
        if(!data[0].equals("brand") || !data[1].equals("category")){
            throw new ApiException("Invalid data headings");
        }
    }

}
