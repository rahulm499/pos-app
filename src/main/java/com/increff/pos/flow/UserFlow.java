package com.increff.pos.flow;

import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

@Service
public class UserFlow {
    @Autowired
    private UserApi userApi;

    @Transactional(rollbackFor = ApiException.class)
    public void add(UserPojo userPojo) throws ApiException {
        if(userApi.get(userPojo.getEmail())!=null){
            throw new ApiException("User with given email already exists");
        }

        userApi.add(userPojo);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void update(UserPojo userPojo) throws ApiException {
        userApi.getCheck(userPojo.getEmail());
        userApi.update(userPojo);
    }

    @Transactional(rollbackFor = IOException.class)
    public String getRole(String email) throws IOException {
        Properties emailProperties = new Properties();
        emailProperties.load(new FileInputStream("email.properties"));
        for (String emailProp : emailProperties.stringPropertyNames()) {
            if(Objects.equals(email, emailProp)){
                return "supervisor";
            }
        }
        return "operator";
    }
}
