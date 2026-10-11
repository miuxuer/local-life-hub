package com.llh.service;

import com.llh.dto.UserLoginDTO;
import com.llh.entity.User;

public interface UserService {
    User wxLogin(UserLoginDTO userLoginDTO);
}
