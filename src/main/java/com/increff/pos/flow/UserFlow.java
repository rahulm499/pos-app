package com.increff.pos.flow;

import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserApiService;
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
    private UserApiService userApiService;

    @Transactional(rollbackFor = ApiException.class)
    public void add(UserPojo userPojo) throws ApiException {
        if(userApiService.get(userPojo.getEmail())!=null){
            throw new ApiException("User with given email already exists");
        }

        userApiService.add(userPojo);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void update(UserPojo userPojo) throws ApiException {
        userApiService.getCheck(userPojo.getEmail());
        userApiService.update(userPojo);
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
