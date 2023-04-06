package com.increff.pos.helper;

import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.api.ApiException;
import com.increff.pos.util.StringUtil;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductHelperUtil {
    public static ProductData convertProduct(ProductPojo productPojo, BrandPojo brandPojo) {
        ProductData data =new ProductData();
        data.setId(productPojo.getId());
        data.setName(productPojo.getName());
        data.setBarcode(productPojo.getBarcode());
        data.setMrp(productPojo.getMrp());
        data.setBrandName(brandPojo.getBrand());
        data.setBrandCategory(brandPojo.getCategory());
        return data;
    }

    // BrandData -> BrandPojo
    public static ProductPojo convertProduct(ProductForm f) {
        ProductPojo p = new ProductPojo();
        p.setBarcode(f.getBarcode());
        p.setName(f.getName());
        p.setMrp(f.getMrp());
        return p;
    }

    public static void productFormValidation(ProductForm form) throws ApiException {
        if(StringUtil.isEmpty(form.getBarcode())){
            throw new ApiException("Barcode cannot be empty");
        }
        if(StringUtil.isEmpty(form.getBrandName())){
            throw new ApiException("Brand Name cannot be empty");
        }
        if(StringUtil.isEmpty(form.getBrandCategory())){
            throw new ApiException("Category cannot be empty");
        }
        if(StringUtil.isEmpty(form.getName())){
            throw new ApiException("Product Name cannot be empty");
        }
        if(form.getMrp() == null){
            throw new ApiException("Invalid MRP");
        }
        if(form.getMrp()<=0){
            throw new ApiException("MRP must be positive");
        }
        if(form.getMrp()>100000000){
            throw new ApiException("MRP cannot be greater than 100000000");
        }
    }
    public static ProductForm productFormNormalize(ProductForm form){
        DecimalFormat df = new DecimalFormat("#.##");
        form.setMrp(Double.parseDouble(df.format(form.getMrp())));
        form.setBarcode(StringUtil.toLowerCase(form.getBarcode()));
        form.setName(StringUtil.toLowerCase(form.getName()));
        form.setBrandCategory(StringUtil.toLowerCase(form.getBrandCategory()));
        form.setBrandName(StringUtil.toLowerCase(form.getBrandName()));
        return form;
    }

    public static void updateProductFormValidation(ProductForm form) throws ApiException {
        if(StringUtil.isEmpty(form.getName())){
            throw new ApiException("Product Name cannot be empty");
        }
        if(form.getMrp() == null){
            throw new ApiException("Invalid MRP");
        }
        if(form.getMrp()<=0){
            throw new ApiException("MRP must be positive");
        }
        if(form.getMrp()>100000000){
            throw new ApiException("MRP cannot be greater than 100000000");
        }
    }
    public static ProductForm updateProductFormNormalize(ProductForm form){
        DecimalFormat df = new DecimalFormat("#.##");
        form.setMrp(Double.parseDouble(df.format(form.getMrp())));
        form.setName(StringUtil.toLowerCase(form.getName()));
        return form;
    }


    public static ErrorData createErrorData(ProductForm productForm, String message, Integer index){
        ErrorData errorData = new ErrorData();
        errorData.setId(index);
        errorData.setMessage(message);
        List<Object> values = new ArrayList<>();
        values.add(productForm.getName());
        values.add(productForm.getBrandName());
        values.add(productForm.getBrandCategory());
        values.add(productForm.getBarcode());
        values.add(productForm.getMrp());
        errorData.setValues(values);
        return errorData;
    }

    public static List<ProductForm> extractFileProductData(MultipartFile file) throws ApiException {
        List<ProductForm> productFormList = new ArrayList<>();
        int index=0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                ProductForm product = new ProductForm();
                if(index ==0){
                    vaidateHeadings(data);
                    index++;
                    continue;
                }
                product.setName(data[0]);
                product.setBrandName(data[1]);
                product.setBrandCategory(data[2]);
                product.setBarcode(data[3]);
                product.setMrp(Double.parseDouble(data[4]));
                productFormList.add(product);
                index++;
            }

        } catch (IOException |ApiException e) {
            throw new ApiException(e.getMessage());
        }
        return productFormList;
    }

    public static ByteArrayOutputStream convertToTSVFile(List<ErrorData> productErrorDataList) throws ApiException {
        StringBuilder tsvData = new StringBuilder();

        // Append the header row (if any)
        // Assumes that the objects in the list have properties that correspond to the column names
        tsvData.append("#\tname\tbrandName\tbrandCategory\tbarcode\tmrp\tmessage\n");

        // Loop through the list of objects and append the data to the StringBuilder
        for (ErrorData errorData : productErrorDataList) {
            // Assumes that the objects in the list have getters that correspond to the column names
            tsvData.append(errorData.getId()).append("\t")
                    .append(errorData.getValues().get(0)).append("\t")
                    .append(errorData.getValues().get(1)).append("\t")
                    .append(errorData.getValues().get(2)).append("\t")
                    .append(errorData.getValues().get(3)).append("\t")
                    .append(errorData.getValues().get(4)).append("\t")
                    .append(errorData.getMessage()).append("\n");

        }
        // create a byte array output stream and write the TSV data to it
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(tsvData.toString().getBytes());
        }catch (IOException e){
            throw new ApiException("Unable to convert Data to TSV file");
        }
        return outputStream;

    }
    private static void vaidateHeadings(String[] data) throws ApiException {
        if(!data[0].equals("name") || !data[1].equals("brandName") || !data[2].equals("brandCategory") || !data[3].equals("barcode") || !data[4].equals("mrp")){
            throw new ApiException("Invalid data headings");
        }
    }
}
