package com.increff.pos.dto;

import com.increff.pos.flow.SignupFlow;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SignupDto {
    @Autowired
    private SignupFlow signupFlow;

    public UserPojo add(UserForm userForm) throws ApiException, IOException {
        normalize(userForm);
        validate(userForm);
        UserPojo userPojo = convertSignupForm(userForm);
        signupFlow.add(userPojo);
        return userPojo;
    }

    protected UserPojo convertSignupForm(UserForm userForm) throws IOException {
        UserPojo userPojo = new UserPojo();
        userPojo.setEmail(userForm.getEmail());
        userPojo.setPassword(userForm.getPassword());
        userPojo.setRole(signupFlow.getRole(userForm.getEmail()));
        return userPojo;
    }
    protected void normalize(UserForm userForm){
        userForm.setEmail(StringUtil.toLowerCase(userForm.getEmail()));
    }
    protected void validate(UserForm userForm) throws ApiException {
        System.out.println(userForm.getEmail());
        if(StringUtil.isEmpty(userForm.getEmail())){
            throw new ApiException("PLease enter a valid email");
        }
        if(userForm.getPassword().length() < 5){
            throw new ApiException("PLease enter a password with 5 or more characters");
        }
    }
    private static final String EMAIL_REGEX =
            "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean isValidEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
}
