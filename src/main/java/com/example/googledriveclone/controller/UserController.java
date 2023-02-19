package com.example.googledriveclone.controller;

import com.example.googledriveclone.dto.UserRequest;
import com.example.googledriveclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/")
    public String index(Model model){
        var user =userRepository.findByUsername("1234");
        System.out.println(user);
        model.addAttribute("user", user);
        return "index";
    }

    @GetMapping("/profile")
    public String profile(Model model){
        model.addAttribute("title", "Форма входа");
        return "profile";
    }

}
