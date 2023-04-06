package com.increff.pos.controller;

import com.increff.pos.AbstractUnitTest;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.api.ApiException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import static org.junit.Assert.assertEquals;


public class SignupControllerTest extends AbstractUnitTest {

    @Autowired
    private SignupController signupController;

    @Test
    public void signupTest() throws ApiException {
        UserForm userForm = new UserForm();
        userForm.setEmail("test@example.com");
        userForm.setPassword("test@pass");
        ModelAndView mav = signupController.signup(userForm);
        assertEquals("redirect:/site/login", mav.getViewName());
    }
    @Test
    public void signupSupervisorTest() throws ApiException {
        UserForm userForm = new UserForm();
        userForm.setEmail("admin@increff.com");
        userForm.setPassword("test@pass");
        ModelAndView mav = signupController.signup(userForm);
        assertEquals("redirect:/site/login", mav.getViewName());
    }

    @Test
    public void signupSadPathWrongPasswordTest(){
        UserForm userForm = new UserForm();
        userForm.setEmail("test@example.com");
        userForm.setPassword("test");
        try {
            ModelAndView mav = signupController.signup(userForm);
        }catch (ApiException e){
            assertEquals("PLease enter a password with 5 or more characters", e.getMessage());
        }

    }
    @Test
    public void signupSadPathEmptyEmailTest(){
        UserForm userForm = new UserForm();
        userForm.setEmail("");
        userForm.setPassword("test");
        try {
            ModelAndView mav = signupController.signup(userForm);
        }catch (ApiException e){
            assertEquals("Email cannot be empty", e.getMessage());
        }
    }
    @Test
    public void signupSadPathWrongEmailTest(){
        UserForm userForm = new UserForm();
        userForm.setEmail("testemail");
        userForm.setPassword("test");
        try {
            ModelAndView mav = signupController.signup(userForm);
        }catch (ApiException e){
            assertEquals("Email is invalid", e.getMessage());
        }
    }
    @Test
    public void signupSadPathDuplicateEmailTest() throws ApiException {
        UserForm userForm = new UserForm();
        userForm.setEmail("test@example.com");
        userForm.setPassword("test@pass");
        signupController.signup(userForm);
        userForm = new UserForm();
        userForm.setEmail("test@example.com");
        userForm.setPassword("test@pass");
        try {
            ModelAndView mav = signupController.signup(userForm);
        }catch (ApiException e){
            assertEquals("User with given email already exists", e.getMessage());
        }
    }
}
