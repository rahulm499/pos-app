package com.increff.pos.controller;

import com.increff.pos.dto.SignupDto;
import com.increff.pos.model.data.InfoData;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;


@Controller
public class SignupController extends AbstractUiController{

	@Autowired
	private SignupDto dto;
	@Autowired
	private InfoData info;
	@Autowired
	private SiteUiController siteUiController;
	@ApiOperation(value = "Creates a user")
	@RequestMapping(path = "/session/signup", method = RequestMethod.POST)
	public ModelAndView signup(@RequestBody UserForm userForm) throws ApiException, IOException {
			dto.add(userForm);
			return siteUiController.logout();
	}


}
