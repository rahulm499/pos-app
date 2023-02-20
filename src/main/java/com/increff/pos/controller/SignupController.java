package com.increff.pos.controller;

import com.increff.pos.dto.UserDto;
import com.increff.pos.model.data.InfoData;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;


@Controller
public class SignupController extends AbstractUiController{

	@Autowired
	private UserDto dto;
	@Autowired
	private InfoData info;
	@Autowired
	private SiteUiController siteUiController;
	@ApiOperation(value = "Creates a user")
	@RequestMapping(path = "/session/signup", method = RequestMethod.POST)
	public ModelAndView signup(@RequestBody UserForm userForm) throws ApiException, IOException {
			dto.add(userForm);
			return new ModelAndView("redirect:/site/login");
	}


}
