package com.increff.pos.controller;

import com.increff.pos.AbstractUnitTest;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.testUtilHelper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import javax.servlet.ServletException;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

public class ProductControllerTest extends AbstractUnitTest {
    @Autowired
    private ProductController productController;
    @Autowired
    private BrandDao brandDao;

    @Before
    public void setup(){
        BrandPojo brandPojo= new BrandPojo();
        brandPojo.setBrand("brand1");
        brandPojo.setCategory("cat1");
        brandDao.insert(brandPojo);
    }

    @Test //Happy Path
    public void testHappyPathGetAllProduct() throws ApiException {
        productController.add(testUtilHelper.getDummyProductForm("bar1", "brand1", "cat1", "prod1", 100.0));
        productController.add(testUtilHelper.getDummyProductForm("bar2", "brand1", "cat1", "prod2", 1000.0));
        List<ProductData> data = productController.getAll();
        assertEquals(2, data.size());
    }
    @Test //Happy Path
    public void testHappyPathAddProduct() throws ApiException {
        ProductForm form = testUtilHelper.getDummyProductForm("bar1", "brand1", "cat1", "prod1", 100.00);
        productController.add(form);
        List<ProductData> data = productController.getAll();
        assertEquals(1, data.size());
        assertEquals(form.getBarcode(), data.get(0).getBarcode());
        assertEquals(form.getName(), data.get(0).getName());
        assertEquals(form.getMrp(), data.get(0).getMrp());
        assertEquals(form.getBrandName(), data.get(0).getBrandName());
        assertEquals(form.getBrandCategory(), data.get(0).getBrandCategory());
    }

    @Test //Sad Path
    public void testSadPathAddProductBarcodeEmpty() throws ApiException {
        try{productController.add(testUtilHelper.getDummyProductForm("", "brand1", "cat1", "prod1", 100.00));}
        catch(ApiException e){ assertEquals("Barcode cannot be empty", e.getMessage()); }
    }
    @Test //Sad Path
    public void testSadPathAddProductBrandNameEmpty() throws ApiException {

        try{productController.add(testUtilHelper.getDummyProductForm("bar1", "", "cat1", "prod1", 100.00));}
        catch(ApiException e){ assertEquals("Brand Name cannot be empty", e.getMessage()); }
  }
    @Test //Sad Path
    public void testSadPathAddProductCategoryNameEmpty() throws ApiException {
        try{productController.add(testUtilHelper.getDummyProductForm("bar1", "brand1", "", "prod1", 100.00));}
        catch(ApiException e){ assertEquals("Category cannot be empty", e.getMessage()); }
    }
    @Test //Sad Path
    public void testSadPathAddProductNameEmpty() throws ApiException {
        try{productController.add(testUtilHelper.getDummyProductForm("bar1", "brand1", "cat1", "", 100.00));}
        catch(ApiException e){ assertEquals("Product Name cannot be empty", e.getMessage()); }
    }
    @Test //Sad Path
    public void testSadPathAddProductMrp() throws ApiException {
        try{productController.add(testUtilHelper.getDummyProductForm("bar1", "brand1", "cat1", "prod1", -1.0));}
        catch(ApiException e){ assertEquals("MRP must be positive", e.getMessage()); }

        try{productController.add(testUtilHelper.getDummyProductForm("bar1", "brand1", "cat1", "prod1", 1000000000.0));}
        catch(ApiException e){ assertEquals("MRP cannot be greater than 100000000", e.getMessage()); }

    }
    @Test //Sad Path
    public void testSadPathAddProductBrandNotExists() throws ApiException {
        try{productController.add(testUtilHelper.getDummyProductForm("bar1", "brand2", "cat2", "prod1", 100.0));}
        catch(ApiException e){ assertEquals("Brand Category Pair does not exists", e.getMessage()); }
    }
    @Test //Sad Path
    public void testSadPathAddProductUniqueBarcode() throws ApiException {
        try{productController.add(testUtilHelper.getDummyProductForm("bar1", "brand1", "cat1", "prod1", 100.0));
            productController.add(testUtilHelper.getDummyProductForm("bar1", "brand1", "cat1", "prod2", 1000.0));}
        catch(ApiException e){ assertEquals("Barcode must be unique", e.getMessage()); }

    }

    @Test //Happy Path
    public void testHappyPathGetProduct() throws ApiException {
        productController.add(testUtilHelper.getDummyProductForm("bar1", "brand1", "cat1", "prod1", 100.0));
        List<ProductData> data = productController.getAll();
        assertEquals(1, data.size());
        ProductData productData = productController.get(data.get(0).getId());
        assertEquals("bar1", productData.getBarcode());
        assertEquals("brand1", productData.getBrandName());
        assertEquals("cat1", productData.getBrandCategory());
        assertEquals("prod1", productData.getName());
    }

    @Test //Sad Path
    public void testSadPathGetProduct() throws ApiException {
        productController.add(testUtilHelper.getDummyProductForm("bar1", "brand1", "cat1", "prod1", 100.0));
        List<ProductData> data = productController.getAll();
        int id = data.get(0).getId()+1;
        try{productController.get(id);}
        catch(ApiException e){ assertEquals("Product with given ID does not exists, id: " + id, e.getMessage()); }

    }
    @Test //Happy Path
    public void testHappyPathUpdateProduct() throws ApiException {
        productController.add(testUtilHelper.getDummyProductForm("bar1", "brand1", "cat1", "prod1", 100.0));
        productController.add(testUtilHelper.getDummyProductForm("bar2", "brand1", "cat1", "prod1", 100.0));
        List<ProductData> data = productController.getAll();
        Double mrp =1250.0;
        ProductForm form = testUtilHelper.getDummyProductForm("bar2", "brand2", "cat2", "prod2", mrp);
        productController.update(data.get(0).getId(), form);
        ProductData productData = productController.get(data.get(0).getId());
        assertEquals(2, productController.getAll().size());
        assertEquals("bar1", productData.getBarcode());
        assertEquals("brand1", productData.getBrandName());
        assertEquals("cat1", productData.getBrandCategory());
        assertEquals("prod2", productData.getName());
        assertEquals(mrp, productData.getMrp());
    }

    @Test //Sad Path
    public void testSadPathUpdateProductNameEmpty() throws ApiException {
        productController.add(testUtilHelper.getDummyProductForm("bar1", "brand1", "cat1", "prod1", 100.0));
        List<ProductData> data = productController.getAll();
        try{Double mrp =1250.0;
            ProductForm form = testUtilHelper.getDummyProductForm("", "brand2", "cat2", "", mrp);
            productController.update(data.get(0).getId(), form);}
        catch(ApiException e){assertEquals("Product Name cannot be empty",e.getMessage());}
    }
    @Test //Sad Path
    public void testSadPathUpdateProductMrp() throws ApiException {
        productController.add(testUtilHelper.getDummyProductForm("bar1", "brand1", "cat1", "prod1", 100.0));
        List<ProductData> data = productController.getAll();

        try{Double mrp =0.0;
            ProductForm form = testUtilHelper.getDummyProductForm("", "brand2", "cat2", "prod2", mrp);
            productController.update(data.get(0).getId(), form);}
        catch(ApiException e){assertEquals("MRP must be positive",e.getMessage());}

        try{Double mrp =1000000000.0;
            ProductForm form = testUtilHelper.getDummyProductForm("", "brand2", "cat2", "prod2", mrp);
            productController.update(data.get(0).getId(), form);}
        catch(ApiException e){assertEquals("MRP cannot be greater than 100000000",e.getMessage());}

    }
    @Test //Sad Path
    public void testSadPathUpdateProductIdNotExists() throws ApiException {
        productController.add(testUtilHelper.getDummyProductForm("bar1", "brand1", "cat1", "prod1", 100.0));
        List<ProductData> data = productController.getAll();
        int id =data.get(0).getId()+1;
        try{Double mrp =1250.0;
            ProductForm form = testUtilHelper.getDummyProductForm("", "brand2", "cat2", "prod1", mrp);
            productController.update(id, form);}
        catch(ApiException e){assertEquals("Product with given ID does not exists, id: " + id,e.getMessage());}
    }

    @Test
    public void testAddBulk() throws ServletException, IOException, ApiException, IllegalAccessException {
        MockMultipartFile mockFile = new MockMultipartFile("file", "testfile.tsv", "text/tsv", "name\tbrandName\tbrandCategory\tbarcode\tmrp\nbiscuit\tbrand1\tcat1\tbis1\t10.00".getBytes());
        productController.addBulk(mockFile);
        List<ProductData> productDataList = productController.getAll();
        assertEquals(1, productDataList.size());
    }

    @Test
    public void testSadPathAddBulk() throws ServletException, IOException, ApiException, IllegalAccessException {
        // Call the method that handles the multipart request
        // Pass in the mocked HttpServletRequest object
        try {
            MockMultipartFile mockFile = new MockMultipartFile("file", "testfile.tsv", "text/tsv", "name\tbrand\tbrandCategory\tbarcode\tmrp\nbiscuit\tbrand1\tcat1\tbis1\t10.00".getBytes());
            productController.addBulk(mockFile);
        }catch (ApiException e){
            assertEquals("Invalid data headings", e.getMessage());
        }
    }
}
