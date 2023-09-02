package com.jegan.blogapplication.service;

import com.jegan.blogapplication.dao.UserRepository;
import com.jegan.blogapplication.entity.Post;
import com.jegan.blogapplication.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository=userRepository;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User findUserByName(String name) {
        return userRepository.findUserNyName(name);
    }

    @Override
    public User findUserById(int authorId) {
        Optional<User> result=userRepository.findById(authorId);
        User user=null;
        if (result.isPresent()) {
            user=result.get();
        } else {
            throw new RuntimeException("Invalid Id");
        }
        return user;
    }
}
