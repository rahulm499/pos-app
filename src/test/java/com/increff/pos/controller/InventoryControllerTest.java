package com.increff.pos.controller;

import com.increff.pos.AbstractUnitTest;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.testUtilHelper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class InventoryControllerTest extends AbstractUnitTest {
    @Autowired
    private BrandDao brandDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private InventoryController controller;
    @Before
    public void setup(){
        BrandPojo brandPojo= new BrandPojo();
        brandPojo.setBrand("brand1");
        brandPojo.setCategory("cat1");
        brandDao.insert(brandPojo);

        ProductPojo productPojo = new ProductPojo();
        productPojo.setBrand_category(brandPojo.getId());
        productPojo.setName("prod1");
        productPojo.setBarcode("bar1");
        productPojo.setMrp(100.00);
        productDao.insert(productPojo);

        productPojo = new ProductPojo();
        productPojo.setBrand_category(brandPojo.getId());
        productPojo.setName("prod2");
        productPojo.setBarcode("bar2");
        productPojo.setMrp(1000.00);
        productDao.insert(productPojo);
    }

    @Test
    public void testGetAllInventory() throws ApiException {
        controller.add(testUtilHelper.getDummyInventoryForm("bar1", 1000));
        controller.add(testUtilHelper.getDummyInventoryForm("bar2", 2000));
        List<InventoryData> data = controller.getAll();
        assertEquals(2, data.size());
    }
    @Test
    public void testHappyPathAddInventory() throws ApiException {
        controller.add(testUtilHelper.getDummyInventoryForm("bar1", 1000));
        controller.add(testUtilHelper.getDummyInventoryForm("bar2", 2000));
        controller.add(testUtilHelper.getDummyInventoryForm("bar2", 2000));
        List<InventoryData> data = controller.getAll();
        Collections.sort(data, (d1, d2) -> d1.getId() -d2.getId());
        assertEquals(2, data.size());
        assertEquals("bar1", data.get(0).getBarcode());
        assertEquals("bar2", data.get(1).getBarcode());
        assertEquals((Integer)4000, data.get(1).getQuantity());
    }

    @Test
    public void testSadPathAddInventoryBarcodeEmpty() throws ApiException {
        try{controller.add(testUtilHelper.getDummyInventoryForm("", 1000));}
        catch(ApiException e){ assertEquals("Barcode cannot be empty", e.getMessage());}
    }
    @Test
    public void testSadPathAddInventoryQuantityEmpty() throws ApiException {
        try{controller.add(testUtilHelper.getDummyInventoryForm("bar1", 0));}
        catch(ApiException e){ assertEquals("Quantity cannot be less than 0", e.getMessage());}
    }

    @Test
    public void testSadPathAddInventoryProductNotExists() throws ApiException {
        try{controller.add(testUtilHelper.getDummyInventoryForm("bar3", 1000));}
        catch(ApiException e){ assertEquals("Product with given barcode does not exists, Barcode: " + "bar3", e.getMessage());}
    }

//

    @Test
    public void testHappyPathGetInventory() throws ApiException {
        controller.add(testUtilHelper.getDummyInventoryForm("bar1", 1000));
        controller.add(testUtilHelper.getDummyInventoryForm("bar2", 2000));
        List<InventoryData> data = controller.getAll();

        assertEquals(data.get(0).getBarcode(), controller.get(data.get(0).getId()).getBarcode());
        assertEquals(data.get(1).getBarcode(), controller.get(data.get(1).getId()).getBarcode());
    }

    @Test
    public void testSadPathGetInventory() throws ApiException {
        controller.add(testUtilHelper.getDummyInventoryForm("bar1", 1000));
        int id = controller.getAll().get(0).getId()+1;
        try{controller.get(id);}
        catch(ApiException e){ assertEquals("Inventory with given ID does not exists, id: " + id, e.getMessage());}
    }

    @Test
    public void testHappyPathUpdateInventory() throws ApiException {
        controller.add(testUtilHelper.getDummyInventoryForm("bar1", 1000));
        InventoryForm form= testUtilHelper.getDummyInventoryForm("bar2", 500);
        controller.update(controller.getAll().get(0).getId(), form);
        List<InventoryData> data = controller.getAll();
        Collections.sort(data, (d1, d2) -> d1.getId() -d2.getId());
        assertEquals("bar1", data.get(0).getBarcode());
        assertEquals((Integer)500, data.get(0).getQuantity());
    }

    @Test
    public void testSadPathUpdateInventoryBarcodeEmpty() throws ApiException {
        controller.add(testUtilHelper.getDummyInventoryForm("bar1", 1000));
        try{ InventoryForm form= testUtilHelper.getDummyInventoryForm("", 500);
            controller.update(controller.getAll().get(0).getId(), form);}
        catch(ApiException e){ assertEquals("Barcode cannot be empty", e.getMessage());}
    }

    @Test
    public void testSadPathUpdateInventoryQuantityEmpty() throws ApiException {
        controller.add(testUtilHelper.getDummyInventoryForm("bar1", 1000));
        try{InventoryForm form= testUtilHelper.getDummyInventoryForm("bar2", 0);
            controller.update(controller.getAll().get(0).getId(), form);}
        catch(ApiException e){ assertEquals("Quantity cannot be less than 0", e.getMessage());}
    }

    @Test
    public void testSadPathUpdateInventoryNotExists() throws ApiException {
        controller.add(testUtilHelper.getDummyInventoryForm("bar1", 1000));
        int id = controller.getAll().get(0).getId()+1;
        try{InventoryForm form= testUtilHelper.getDummyInventoryForm("bar2", 100);
            controller.update(id, form);}
        catch(ApiException e){ assertEquals("Inventory with given ID does not exists, id: " + id, e.getMessage());}

    }

    @Test
    public void testAddBulk() throws ServletException, IOException, ApiException, IllegalAccessException {
        MockMultipartFile mockFile = new MockMultipartFile("file", "testfile.tsv", "text/tsv", "barcode\tquantity\nbar1\t100".getBytes());
        controller.addBulk(mockFile);
        List<InventoryData> inventoryDataList = controller.getAll();
        assertEquals(1, inventoryDataList.size());
    }

    @Test
    public void testSadPathAddBulk() throws ServletException, IOException, ApiException, IllegalAccessException {
        // Call the method that handles the multipart request
        // Pass in the mocked HttpServletRequest object
        try {
            MockMultipartFile mockFile = new MockMultipartFile("file", "testfile.tsv", "text/tsv", "bar\tquantity\nbar1\t100".getBytes());
            controller.addBulk(mockFile);
        }catch (ApiException e){
            assertEquals("Invalid data headings", e.getMessage());
        }
    }

}
