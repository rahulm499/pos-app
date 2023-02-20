package com.increff.pos.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.increff.pos.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.model.data.UserData;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class AdminApiController {

	@Autowired
	private UserDto dto;

	@ApiOperation(value = "Adds a user")
	@RequestMapping(path = "/api/admin/user", method = RequestMethod.POST)
	public void addUser(@RequestBody UserForm form) throws ApiException, IOException {
		dto.add(form);
	}

	@ApiOperation(value = "Deletes a user")
	@RequestMapping(path = "/api/admin/user/{id}", method = RequestMethod.DELETE)
	public void deleteUser(@PathVariable int id) throws IOException, ApiException {
		dto.delete(id);
	}

	@ApiOperation(value = "Updates a user")
	@RequestMapping(path = "/api/admin/user/{id}", method = RequestMethod.PUT)
	public void updateUser(@PathVariable Integer id, @RequestBody UserForm form) throws IOException, ApiException {
		dto.update(form, id);
	}

	@ApiOperation(value = "Gets list of all users")
	@RequestMapping(path = "/api/admin/user", method = RequestMethod.GET)
	public List<UserData> getAllUser() throws IOException, ApiException {
		return dto.getAll();
	}
	@ApiOperation(value = "Gets a user")
	@RequestMapping(path = "/api/admin/user/{id}", method = RequestMethod.GET)
	public UserData get(@PathVariable Integer id) throws IOException, ApiException {
		return dto.get(id);
	}


}
