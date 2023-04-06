package com.increff.pos.controller;

import com.increff.pos.AbstractUnitTest;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.api.ApiException;
import com.increff.pos.testUtilHelper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;



public class BrandControllerTest extends AbstractUnitTest {

    @Autowired
    private BrandController controller;

    @Test
    public void testGetAllBrand() throws ApiException {
        controller.add(testUtilHelper.getDummyBrandForm("brand1", "category1"));
        controller.add(testUtilHelper.getDummyBrandForm("brand2", "category2"));
        List<BrandData> brandDataList = controller.getAll();
        assertEquals(2, brandDataList.size());
    }
    @Test //Happy Path
    public void testHappyPathAddBrand() throws ApiException {
        controller.add(testUtilHelper.getDummyBrandForm("brand1", "category1"));
        controller.add(testUtilHelper.getDummyBrandForm("BrANd2", "cAtEgOry2"));
        controller.add(testUtilHelper.getDummyBrandForm("123+", "456"));
        List<BrandData> brandDataList = controller.getAll();
        Collections.sort(brandDataList, (d1, d2) -> d1.getId() -d2.getId());
        assertEquals(3, brandDataList.size());
        assertEquals("brand1", brandDataList.get(0).getBrand());
        assertEquals("category1", brandDataList.get(0).getCategory());
        assertEquals("brand2", brandDataList.get(1).getBrand());
        assertEquals("category2", brandDataList.get(1).getCategory());
        assertEquals("123+", brandDataList.get(2).getBrand());
        assertEquals("456", brandDataList.get(2).getCategory());
    }

    @Test //Sad Path
    public void testSadPathAddBrandNameEmpty(){
        try {
            controller.add(testUtilHelper.getDummyBrandForm("", "cAtEgOry2"));
        } catch (ApiException e) {
            assertEquals("Brand cannot be empty", e.getMessage());}

        try {controller.add(new BrandForm());} catch (ApiException e) {
            assertEquals("Brand cannot be empty", e.getMessage());}
    }
    @Test //Sad Path
    public void testSadPathAddBrandCategoryEmpty(){
        // Brand Category Empty
        try {controller.add(testUtilHelper.getDummyBrandForm("brand1", ""));} catch (ApiException e) {
            assertEquals("Category cannot be empty", e.getMessage());}

    }

    @Test //Sad Path
    public void testSadPathAddBrandCategoryExists() {
        // Unique check
        try {controller.add(testUtilHelper.getDummyBrandForm("brand1", "category1"));
            controller.add(testUtilHelper.getDummyBrandForm("brand1", "category1"));} catch (ApiException e) {
            assertEquals("Brand Category Pair already exists", e.getMessage());}
    }

    @Test // Happy Path
    public void testHappyPathGetBrand() throws ApiException {
        controller.add(testUtilHelper.getDummyBrandForm("brand1", "category1"));
        List<BrandData> brandDataList = controller.getAll();

        BrandData brandData = controller.get(brandDataList.get(0).getId());
        assertEquals(brandDataList.get(0).getId(), brandData.getId());
        assertEquals("brand1", brandData.getBrand());
        assertEquals("category1", brandData.getCategory());
    }
    @Test // Sad Path
    public void testSadPathGetBrand() throws ApiException {
        controller.add(testUtilHelper.getDummyBrandForm("brand1", "category1"));
        List<BrandData> brandDataList = controller.getAll();
        int id = brandDataList.get(0).getId()+1;
        try{
            controller.get(id);
        }catch (ApiException e) {
            assertEquals("Brand with given ID does not exists, id: " + id, e.getMessage());
        }

    }
    @Test // Happy Path
    public void testHappyPathUpdateBrand() throws ApiException {
        controller.add(testUtilHelper.getDummyBrandForm("brand1", "category1"));
        BrandForm form = testUtilHelper.getDummyBrandForm("updatedbrand1", "updatedcategory1");
        List<BrandData> brandDataList = controller.getAll();
        Collections.sort(brandDataList, (d1, d2) -> d1.getId() -d2.getId());
        controller.update(brandDataList.get(0).getId(), form);
        List<BrandData> updatedBrandDataList = controller.getAll();
        assertEquals(brandDataList.get(0).getId(), updatedBrandDataList.get(0).getId());
        assertEquals("updatedbrand1", updatedBrandDataList.get(0).getBrand());
        assertEquals("updatedcategory1", updatedBrandDataList.get(0).getCategory());
    }
    @Test // Sad Path
    public void testSadPathUpdateBrandNameEmpty() throws ApiException {
        controller.add(testUtilHelper.getDummyBrandForm("brand1", "category1"));
        controller.add(testUtilHelper.getDummyBrandForm("brand2", "category2"));
        List<BrandData> brandDataList = controller.getAll();
        Collections.sort(brandDataList, (d1, d2) -> d1.getId() -d2.getId());
        try {BrandForm form = testUtilHelper.getDummyBrandForm("", "category2");
            controller.update(brandDataList.get(0).getId(), form);} catch (ApiException e){
            assertEquals("Brand cannot be empty", e.getMessage());}

        try {controller.add(new BrandForm());} catch (ApiException e) {
            assertEquals("Brand cannot be empty", e.getMessage());}

    }
    @Test // Sad Path
    public void testSadPathUpdateBrandCategoryEmpty() throws ApiException {
        controller.add(testUtilHelper.getDummyBrandForm("brand1", "category1"));
        controller.add(testUtilHelper.getDummyBrandForm("brand2", "category2"));
        List<BrandData> brandDataList = controller.getAll();
        Collections.sort(brandDataList, (d1, d2) -> d1.getId() -d2.getId());
        try {BrandForm form = testUtilHelper.getDummyBrandForm("brand2", "");
            controller.update(brandDataList.get(0).getId(), form);} catch (ApiException e){
            assertEquals("Category cannot be empty", e.getMessage());}
    }

    @Test // Sad Path
    public void testSadPathUpdateBrandCategoryExists() throws ApiException {
        controller.add(testUtilHelper.getDummyBrandForm("brand1", "category1"));
        controller.add(testUtilHelper.getDummyBrandForm("brand2", "category2"));
        List<BrandData> brandDataList = controller.getAll();
        Collections.sort(brandDataList, (d1, d2) -> d1.getId() -d2.getId());
        try {BrandForm form = testUtilHelper.getDummyBrandForm("brand2", "category2");
            controller.update(brandDataList.get(0).getId(), form);} catch (ApiException e){
            assertEquals("Brand Category Pair already exists", e.getMessage());}


    }
    @Test // Sad Path
    public void testSadPathUpdateBrandNotExists() throws ApiException {
        controller.add(testUtilHelper.getDummyBrandForm("brand1", "category1"));
        controller.add(testUtilHelper.getDummyBrandForm("brand2", "category2"));
        List<BrandData> brandDataList = controller.getAll();
        Collections.sort(brandDataList, (d1, d2) -> d1.getId() -d2.getId());
        int id =brandDataList.get(1).getId()+1;
        try {BrandForm form = testUtilHelper.getDummyBrandForm("brand3", "category3");
            controller.update(id, form);} catch (ApiException e){
            assertEquals("Brand with given ID does not exists, id: " + id, e.getMessage());}

    }

    @Test
    public void testAddBulk() throws ApiException {
        MockMultipartFile mockFile = new MockMultipartFile("file", "testfile.tsv", "text/tsv", "brand\tcategory\nbrand\tcategory\nbrand2\tcategory2".getBytes());
        controller.addBulk(mockFile);
        List<BrandData> brandDataList = controller.getAll();
        assertEquals(2, brandDataList.size());
    }

    @Test
    public void testSadPathWrongHeadingAddBulk(){
        try {
            MockMultipartFile mockFile = new MockMultipartFile("file", "testfile.tsv", "text/tsv", "brand\tcat\nbrand\tcategory\nbrand2\tcategory2".getBytes());
            controller.addBulk(mockFile);
        }catch (ApiException e){
            assertEquals("Invalid data headings", e.getMessage());
        }
    }
    @Test
    public void testSadPathWrongDataAddBulk() throws ApiException {
            MockMultipartFile mockFile = new MockMultipartFile("file", "testfile.tsv", "text/tsv", "brand\tcategory\nbrand\tcategory\nbrand\tcategory".getBytes());
            ResponseEntity<byte[]> data =controller.addBulk(mockFile);
            assertNotEquals(0, data.toString().length());
    }

}
