package com.example.googledriveclone.controller;

import com.example.googledriveclone.mapper.UserMapper;
import com.example.googledriveclone.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @Autowired
    private UserMapper userMapper;
    @GetMapping("/")
    public String index(@AuthenticationPrincipal User user, Model model){

        model.addAttribute("user", user);
        return "index";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User user, Model model){

        model.addAttribute("user", userMapper.toDto(user));
        return "profile";
    }


}
