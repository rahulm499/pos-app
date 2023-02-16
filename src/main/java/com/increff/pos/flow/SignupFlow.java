package com.increff.pos.flow;

import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Properties;

@Component
public class SignupFlow {
    @Autowired
    private UserService userService;

    public void add(UserPojo userPojo) throws ApiException {
        userService.add(userPojo);
    }

    public String getRole(String email) throws IOException {
        Properties emailProperties = new Properties();
        emailProperties.load(new FileInputStream("email.properties"));
        for (String emailProp : emailProperties.stringPropertyNames()) {
            System.out.println(emailProp);
            if(Objects.equals(email, emailProp)){
                return "supervisor";
            }
        }
        return "operator";
    }
}
