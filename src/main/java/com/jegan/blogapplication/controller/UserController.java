package com.jegan.blogapplication.controller;

import com.jegan.blogapplication.entity.User;
import com.jegan.blogapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService=userService;
    }
    @GetMapping("/login")
    public String showLogin() {
        return "blogs/login-form";
    }

    @GetMapping("/")
    public String listBlogs() {
        return "redirect:/blogs/list";
    }

    @GetMapping("/userRegister")
    public String userRegister(Model model) {
        model.addAttribute("user", new User());
        return "blogs/user-register";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute("user") User user,@RequestParam("confirmPassword") String confirmPassword) {
        if(user.getPassword().equals(confirmPassword)) {
            user.setRole("ROLE_AUTHOR");
            user.setActive(true);
            user.setPassword("{noop}"+user.getPassword());
            userService.save(user);
        }
        return "redirect:/users/login";
    }
}
