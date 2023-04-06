package com.increff.pos.controller;

import com.increff.pos.AbstractUnitTest;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.api.ApiException;
import com.increff.pos.api.UserApi;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class LoginControllerTest extends AbstractUnitTest {

    @Autowired
    private LoginController loginController;
    @Autowired
    private UserApi userApi;

    @Before
    public void setup() throws ApiException {
        UserPojo user = new UserPojo();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole("operator");
        userApi.add(user);
    }

    @Test
    public void loginTest() throws ApiException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        LoginForm loginForm = new LoginForm();
        loginForm.setEmail("test@example.com");
        loginForm.setPassword("password");
        when(request.getSession(true)).thenReturn(session);
        ModelAndView mav = loginController.login(request, loginForm);
        assertEquals("redirect:/ui/home", mav.getViewName());
    }

    @Test
    public void loginSadPathTest() throws ApiException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        LoginForm loginForm = new LoginForm();
        loginForm.setEmail("test@exaple.com");
        loginForm.setPassword("password");
        when(request.getSession(true)).thenReturn(session);
        ModelAndView mav = loginController.login(request, loginForm);
        assertEquals("redirect:/site/login", mav.getViewName());
    }

    @Test
    public void logoutTestPath() throws ApiException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        LoginForm loginForm = new LoginForm();
        loginForm.setEmail("test@example.com");
        loginForm.setPassword("password");
        when(request.getSession(true)).thenReturn(session);
        loginController.login(request, loginForm);
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        ModelAndView mav = loginController.logout(request);
        assertEquals("redirect:/site/login", mav.getViewName());
    }

}
