package com.example.googledriveclone.controller;

import com.example.googledriveclone.dto.UserRequest;
import com.example.googledriveclone.services.Impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    private String index(Model model){
        model.addAttribute("registrationForm", new UserRequest());
        return "registration";
    }

    @PostMapping("/registration")
    private String registration(@ModelAttribute("registrationForm") @Valid UserRequest userRequest,
                                BindingResult bindingResult
                               ){
        if(bindingResult.hasErrors()){
            return "registration";
        }

        userService.createUser(userRequest);
        return "index";
    }
}
