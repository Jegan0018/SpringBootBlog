package com.jegan.blogapplication.service;

import com.jegan.blogapplication.entity.User;

public interface UserService {

    void save(User user);

    User findUserByName(String name);

    User findUserById(int authorId);
}
