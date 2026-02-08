package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.domain.user.User;

public interface AuthService {
    User register(String name, String email, String password);
}
