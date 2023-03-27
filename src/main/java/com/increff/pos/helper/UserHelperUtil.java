package com.increff.pos.helper;

import com.increff.pos.model.data.*;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.util.StringUtil;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserHelperUtil {
    public static UserPojo convertUserForm(UserForm userForm, String role) throws IOException {
        UserPojo userPojo = convertUserUpdateForm(userForm, role);
        userPojo.setPassword(userForm.getPassword());
        return userPojo;
    }
    public static UserPojo convertUserUpdateForm(UserForm userForm, String role) throws IOException {
        UserPojo userPojo = new UserPojo();
        userPojo.setEmail(userForm.getEmail());
        if(userForm.getRole()==null || userForm.getRole().isEmpty()){
            userPojo.setRole(role);
        }else{
            userPojo.setRole(userForm.getRole());
        }
        return userPojo;
    }

    public static UserData convertUserData(UserPojo userPojo){
        UserData data =new UserData();
        data.setEmail(userPojo.getEmail());
        data.setRole(userPojo.getRole());
        data.setId(userPojo.getId());
        return data;
    }
    public static void normalize(UserForm userForm){
        userForm.setEmail(StringUtil.toLowerCase(userForm.getEmail()));
    }
    public static void validate(UserForm userForm) throws ApiException {
        isValidEmail(userForm.getEmail());
        if(userForm.getPassword().length() < 5){
            throw new ApiException("PLease enter a password with 5 or more characters");
        }
    }
    private static Pattern emailPattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    public static void isValidEmail(String email) throws ApiException {
        if(email == null || email.isEmpty()){
            throw new ApiException("Email cannot be empty");
        }
        Matcher matcher = emailPattern.matcher(email);
        if( !matcher.matches()){
            throw new ApiException("Email is invalid");
        }
    }

}
