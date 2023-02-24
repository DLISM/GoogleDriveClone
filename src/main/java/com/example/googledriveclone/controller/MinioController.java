package com.example.googledriveclone.controller;

import com.example.googledriveclone.services.MinioService;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
public class MinioController {

    @Autowired
    private MinioService minioService;

    @GetMapping("/files")
    public String testMinio(Model model) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        model.addAttribute("files", minioService.objectList());

        return "files";
    }

}
