package com.increff.pos.dto;

import com.increff.pos.flow.UserFlow;
import com.increff.pos.model.data.UserData;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static com.increff.pos.helper.UserHelperUtil.*;

@Service
public class UserDto {
    @Autowired
    private UserFlow userFlow;
    @Autowired
    private UserApi service;
    @Transactional(rollbackFor = ApiException.class)
    public UserPojo add(UserForm userForm) throws ApiException, IOException {
        normalize(userForm);
        validate(userForm);
        UserPojo userPojo = convertUserForm(userForm, userFlow.getRole(userForm.getEmail()));
        userFlow.add(userPojo);
        return userPojo;
    }

    @Transactional(rollbackFor = ApiException.class)
    public void update(UserForm userForm, Integer id) throws ApiException, IOException {
        normalize(userForm);
        isValidEmail(userForm.getEmail());
        UserPojo userPojo = convertUserUpdateForm(userForm, userFlow.getRole(userForm.getEmail()));
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

}
