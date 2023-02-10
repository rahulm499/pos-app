package com.increff.pos.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.pojo.EmployeePojo;

public class EmployeeServiceTest extends AbstractUnitTest {

	@Autowired
	private EmployeeService service;

	@Test
	public void testAdd() throws ApiException {
		EmployeePojo p = new EmployeePojo();
		p.setName(" Romil Jain ");
		service.add(p);
	}

	@Test
	public void testNormalize() {
		EmployeePojo p = new EmployeePojo();
		p.setName(" Romil Jain ");
		EmployeeService.normalize(p);
		assertEquals("romil jain", p.getName());
	}

}
