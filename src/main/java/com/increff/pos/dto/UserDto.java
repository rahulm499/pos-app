package com.increff.pos.dto;

import com.increff.pos.flow.UserFlow;
import com.increff.pos.model.data.UserData;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserApiService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserDto {
    @Autowired
    private UserFlow userFlow;
    @Autowired
    private UserApiService service;
    @Transactional(rollbackFor = ApiException.class)
    public UserPojo add(UserForm userForm) throws ApiException, IOException {
        normalize(userForm);
        validate(userForm);
        UserPojo userPojo = convertUserForm(userForm);
        userFlow.add(userPojo);
        return userPojo;
    }

    @Transactional(rollbackFor = ApiException.class)
    public void update(UserForm userForm, Integer id) throws ApiException, IOException {
        normalize(userForm);
        isValidEmail(userForm.getEmail());
        UserPojo userPojo = convertUserUpdateForm(userForm);
        userPojo.setId(id);
        userFlow.update(userPojo);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void delete(Integer id) throws ApiException{
        service.delete(id);
    }

    @Transactional(rollbackFor = ApiException.class)
    public List<UserData> getAll() throws ApiException{
        List<UserPojo> userPojoList = service.getAll();
        List<UserData> userDataList = new ArrayList<UserData>();
        for (UserPojo userPojo : userPojoList) {
            userDataList.add(convertUserData(userPojo));
        }
        return userDataList;
    }
    @Transactional(rollbackFor = ApiException.class)
    public UserData get(Integer id) throws ApiException{
        return convertUserData(service.getById(id));
    }

    protected UserPojo convertUserForm(UserForm userForm) throws IOException {
        UserPojo userPojo = convertUserUpdateForm(userForm);
        userPojo.setPassword(userForm.getPassword());
        return userPojo;
    }
    protected UserPojo convertUserUpdateForm(UserForm userForm) throws IOException {
        UserPojo userPojo = new UserPojo();
        userPojo.setEmail(userForm.getEmail());
        if(userForm.getRole()==null || userForm.getRole().isEmpty()){
            userPojo.setRole(userFlow.getRole(userForm.getEmail()));
        }else{
            userPojo.setRole(userForm.getRole());
        }
        return userPojo;
    }

    protected UserData convertUserData(UserPojo userPojo){
        UserData data =new UserData();
        data.setEmail(userPojo.getEmail());
        data.setRole(userPojo.getRole());
        data.setId(userPojo.getId());
        return data;
    }
    protected void normalize(UserForm userForm){
        userForm.setEmail(StringUtil.toLowerCase(userForm.getEmail()));
    }
    protected void validate(UserForm userForm) throws ApiException {
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
