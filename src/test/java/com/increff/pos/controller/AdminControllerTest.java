package com.increff.pos.controller;

import com.increff.pos.AbstractUnitTest;
import com.increff.pos.model.data.UserData;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.api.ApiException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import static org.junit.Assert.assertEquals;



public class AdminControllerTest extends AbstractUnitTest {

    @Autowired
    private AdminController adminController;


    @Test
    public void adminAddUserTest() throws ApiException {
        UserForm userForm = new UserForm();
        userForm.setEmail("test@example.com");
        userForm.setPassword("test@pass");
        adminController.addUser(userForm);
        List<UserData> userDataList = adminController.getAllUser();
        assertEquals(1, userDataList.size());
    }

    @Test
    public void adminDeleteUserTest() throws ApiException {
        UserForm userForm = new UserForm();
        userForm.setEmail("test@example.com");
        userForm.setPassword("test@pass");
        adminController.addUser(userForm);
        List<UserData> userDataList = adminController.getAllUser();
        adminController.deleteUser(userDataList.get(0).getId());
        userDataList = adminController.getAllUser();
        assertEquals(0, userDataList.size());
    }
    @Test
    public void adminSadPathDeleteUserTest() throws ApiException {
        UserForm userForm = new UserForm();
        userForm.setEmail("test@example.com");
        userForm.setPassword("test@pass");
        adminController.addUser(userForm);
        List<UserData> userDataList = adminController.getAllUser();
        try{
        adminController.deleteUser(userDataList.get(0).getId()+1);}
        catch (ApiException e){
            assertEquals("User does not exist", e.getMessage());
        }
    }

    @Test
    public void adminGetAllUserTest() throws ApiException {
        UserForm userForm = new UserForm();
        userForm.setEmail("test@example.com");
        userForm.setPassword("test@pass");
        adminController.addUser(userForm);
        userForm = new UserForm();
        userForm.setEmail("test2@example.com");
        userForm.setPassword("test@pass");
        adminController.addUser(userForm);
        List<UserData> userDataList = adminController.getAllUser();
        assertEquals(2, userDataList.size());
    }

    @Test
    public void adminGetUserByIdTest() throws ApiException {
        UserForm userForm = new UserForm();
        userForm.setEmail("test@example.com");
        userForm.setPassword("test@pass");
        adminController.addUser(userForm);
        List<UserData> userDataList = adminController.getAllUser();
        UserData userData = adminController.get(userDataList.get(0).getId());
        assertEquals("test@example.com", userData.getEmail());
    }
    @Test
    public void adminUpdateUserTest() throws ApiException {
        UserForm userForm = new UserForm();
        userForm.setEmail("test@example.com");
        userForm.setPassword("test@pass");
        adminController.addUser(userForm);
        userForm = new UserForm();
        userForm.setEmail("test@example.com");
        userForm.setRole("supervisor");
        List<UserData> userDataList = adminController.getAllUser();
        adminController.updateUser(userDataList.get(0).getId(), userForm);
        UserData userData = adminController.get(userDataList.get(0).getId());
        assertEquals("supervisor", userData.getRole());
    }
}
