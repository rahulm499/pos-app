package com.increff.pos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.increff.pos.model.data.InfoData;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.api.ApiException;
import com.increff.pos.api.UserApi;

import io.swagger.annotations.ApiOperation;

@Controller
public class InitApiController extends AbstractUiController {

    @Autowired
    private UserApi service;
    @Autowired
    private InfoData info;

    @ApiOperation(value = "Initializes application")
    @RequestMapping(path = "/site/init", method = RequestMethod.GET)
    public ModelAndView showPage(UserForm form) throws ApiException {
        info.setMessage("");
        return mav("init.html");
    }

    @ApiOperation(value = "Initializes application")
    @RequestMapping(path = "/site/init", method = RequestMethod.POST)
    public ModelAndView initSite(UserForm form) throws ApiException {
        List<UserPojo> list = service.getAll();
        if (list.size() > 0) {
            info.setMessage("Application already initialized. Please use existing credentials");
        } else {
            form.setRole("admin");
            UserPojo p = convert(form);
            service.add(p);
            info.setMessage("Application initialized");
        }
        return mav("init.html");

    }

    private static UserPojo convert(UserForm f) {
        UserPojo p = new UserPojo();
        p.setEmail(f.getEmail());
        p.setRole(f.getRole());
        p.setPassword(f.getPassword());
        return p;
    }

}
