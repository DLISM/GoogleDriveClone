package com.example.googledriveclone.controller;

import com.example.googledriveclone.dto.UserDto;
import com.example.googledriveclone.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Log4j
public class RegistrationController {

    private UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String showRegistrationForm(Model model){

        model.addAttribute("registrationForm", new UserDto());
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("registrationForm") @Valid UserDto userDto,
                                BindingResult bindingResult,
                                Model model
                               ){
        if(bindingResult.hasErrors()){
            return "registration";
        }

        if(!userDto.getPassword().equals(userDto.getPasswordConfirmation())){
            model.addAttribute("passIsNotConfirm", "Пароли не совпадают!");
            return "registration";
        }
        if(!userService.createUser(userDto)){
            model.addAttribute("userExists", "Пользовател уже существует!");
            return "registration";
        }

        return "redirect:/";
    }
}
