package com.increff.pos.api;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.UserDao;
import com.increff.pos.pojo.UserPojo;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserApi {

    @Autowired
    private UserDao dao;

    @Transactional(rollbackFor = ApiException.class)
    public void add(UserPojo p) throws ApiException {
        if (get(p.getEmail()) != null) {
            throw new ApiException("User with given email already exists");
        }
        dao.insert(p);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void update(UserPojo userPojo) throws ApiException {
        getCheck(userPojo.getEmail());
        UserPojo ex = get(userPojo.getEmail());
        ex.setRole(userPojo.getRole());
    }

    @Transactional(rollbackFor = ApiException.class)
    public void delete(int id) throws ApiException {
        getCheckById(id);
        dao.delete(id);
    }

    public UserPojo get(String email) throws ApiException {
        return dao.select(email);
    }

    public UserPojo getById(Integer id) {
        return dao.select(id);
    }

    public UserPojo getCheck(String email) throws ApiException {
        UserPojo userPojo = get(email);
        if (userPojo == null) {
            throw new ApiException("User does not exist");
        }
        return userPojo;
    }

    public UserPojo getCheckById(Integer id) throws ApiException {
        UserPojo userPojo = getById(id);
        if (userPojo == null) {
            throw new ApiException("User does not exist");
        }
        return userPojo;
    }

    public List<UserPojo> getAll() {
        return dao.selectAll();
    }


}
