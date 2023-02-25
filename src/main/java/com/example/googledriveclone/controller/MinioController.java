package com.example.googledriveclone.controller;

import com.example.googledriveclone.dto.UserDto;
import com.example.googledriveclone.models.User;
import com.example.googledriveclone.services.MinioService;
import io.minio.errors.*;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/files")
@Log4j
public class MinioController {

    @Autowired
    private MinioService minioService;

    @GetMapping("")
    public String getUserFiles(@AuthenticationPrincipal User user, Model model,
                               @RequestParam(value = "createSuccess", required = false) boolean createSuccess,
                               @RequestParam(value = "createFailed", required = false) boolean createFailed
                               ) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        model.addAttribute("user", user);
        model.addAttribute("files", minioService.objectList(user.getUsername()));
        model.addAttribute("createSuccess", createSuccess);
        model.addAttribute("createFailed", createFailed);

        return "files";
    }

    @PostMapping("/create")
    public RedirectView createFolder(@AuthenticationPrincipal User user, @ModelAttribute("folderName") String folderName, RedirectAttributes redirectAttributes) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        var newFolder = user.getUsername()+"/"+folderName;
        var creatFolder = minioService.createFolder(newFolder);
        log.info(creatFolder);
        if(creatFolder)
            redirectAttributes.addAttribute("createSuccess", true);
        else
            redirectAttributes.addAttribute("createFailed", true);

        log.info(newFolder);
        return new RedirectView("/files");
    }
}
