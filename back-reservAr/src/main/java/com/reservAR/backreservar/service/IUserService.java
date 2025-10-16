package com.reservAR.backreservar.service;

import com.reservAR.backreservar.dto.LoginResponse;
import com.reservAR.backreservar.dto.UserLoginDto;
import com.reservAR.backreservar.dto.UserRegisterDto;
import com.reservAR.backreservar.model.User;

public interface IUserService {
    User findById(Long id);
    User register(UserRegisterDto user);
    User registerStaff(UserRegisterDto user);
    LoginResponse login(UserLoginDto user);
}
