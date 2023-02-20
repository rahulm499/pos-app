package com.increff.pos.service;

import java.util.List;

import javax.transaction.Transactional;

import com.increff.pos.pojo.BrandPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.UserDao;
import com.increff.pos.pojo.UserPojo;

@Service
public class UserApiService {

	@Autowired
	private UserDao dao;

	@Transactional
	public void add(UserPojo p) throws ApiException {

		dao.insert(p);
	}
	@Transactional
	public void update(UserPojo userPojo) throws ApiException {
		UserPojo ex = getCheck(userPojo.getEmail());
		ex.setRole(userPojo.getRole());
	}
	@Transactional(rollbackOn = ApiException.class)
	public UserPojo get(String email) throws ApiException {
		return dao.select(email);
	}
	@Transactional(rollbackOn = ApiException.class)
		public UserPojo getById(Integer id) throws ApiException {
		return dao.select(id);
	}
	@Transactional(rollbackOn = ApiException.class)
	public UserPojo getCheck(String email) throws ApiException {
		UserPojo userPojo = get(email);
		if(userPojo==null){
			throw new ApiException("User does not exist");
		}
		return userPojo;
	}
	@Transactional(rollbackOn = ApiException.class)
	public UserPojo getCheckById(Integer id) throws ApiException {
		UserPojo userPojo = getById(id);
		if(userPojo==null){
			throw new ApiException("User does not exist");
		}
		return userPojo;
	}
	@Transactional
	public List<UserPojo> getAll() {
		return dao.selectAll();
	}

	@Transactional
	public void delete(int id) {
		dao.delete(id);
	}


}
