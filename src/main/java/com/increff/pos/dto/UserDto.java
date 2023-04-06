package com.increff.pos.dto;

import com.increff.pos.model.data.UserData;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.api.ApiException;
import com.increff.pos.api.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.increff.pos.helper.UserHelperUtil.*;

@Service
public class UserDto {
    @Autowired
    private UserApi userApi;

    @Transactional(rollbackFor = ApiException.class)
    public UserPojo add(UserForm userForm) throws ApiException {
        normalize(userForm);
        validate(userForm);
        UserPojo userPojo = convertUserForm(userForm, getRole(userForm.getEmail()));
        userApi.add(userPojo);
        return userPojo;
    }

    @Transactional(rollbackFor = ApiException.class)
    public void update(UserForm userForm, Integer id) throws ApiException {
        normalize(userForm);
        isValidEmail(userForm.getEmail());
        UserPojo userPojo = convertUserUpdateForm(userForm, getRole(userForm.getEmail()));
        userPojo.setId(id);
        userApi.update(userPojo);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void delete(Integer id) throws ApiException {
        userApi.delete(id);
    }

    public List<UserData> getAll(){
        List<UserPojo> userPojoList = userApi.getAll();
        List<UserData> userDataList = new ArrayList<UserData>();
        for (UserPojo userPojo : userPojoList) {
            userDataList.add(convertUserData(userPojo));
        }
        return userDataList;
    }

    public UserData get(Integer id) throws ApiException {
        return convertUserData(userApi.getCheckById(id));
    }

}
