package com.example.googledriveclone.controller;

import com.example.googledriveclone.models.User;
import com.example.googledriveclone.services.MinioService;
import io.minio.errors.*;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Controller
@RequestMapping("/files")
@Log4j
public class MinioController {

    @Autowired
    private MinioService minioService;

    @GetMapping("")
    public String getUserFiles(@AuthenticationPrincipal User user,
                               Model model,
                               @RequestParam(value = "createSuccess", required = false) boolean createSuccess,
                               @RequestParam(value = "createFailed", required = false) boolean createFailed,
                               @RequestParam(value = "", required = false) String subdirectory) throws Exception{

        var directory =user.getUsername()+"/";

        if(subdirectory!=null)
            directory=subdirectory;

        model.addAttribute("files", minioService.folderList(directory));
        model.addAttribute("user", user);
        model.addAttribute("createSuccess", createSuccess);
        model.addAttribute("createFailed", createFailed);

        minioService.renameFile("dsds","");
        return "files";
    }

    @PostMapping("/create")
    public RedirectView createFolder(@AuthenticationPrincipal User user,
                                     @ModelAttribute("folderName") String folderName,
                                     RedirectAttributes redirectAttributes) throws Exception {

        var newFolder = user.getUsername()+"/"+folderName;
        var creatFolder = minioService.createFolder(newFolder);

        if(creatFolder) {
            redirectAttributes.addAttribute("createSuccess", true);
            log.info("folder create success");
        }
        else {
            redirectAttributes.addAttribute("createFailed", true);
            log.info("folder create failed");
        }

        return new RedirectView("/files");
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("files") String pathList){
        var pathArray = pathList.split(",");

        String[] deleteFilesPath = Arrays.stream(pathArray)
                .filter(s -> !s.equals(" "))
                .map(s -> s.trim())
                .toArray(String[]::new);

        try {
            minioService.deleteFolder(deleteFilesPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/files";
    }

    @GetMapping("/search")
    public String search(@AuthenticationPrincipal User user, Model model, @RequestParam("query") String query) throws Exception {

        model.addAttribute("user", user);
        model.addAttribute("files", minioService.search(user.getUsername(), query));
        model.addAttribute("query", query);

        return "search";
    }

    @PostMapping("/upload")
    public String upload(@AuthenticationPrincipal User user,
                         @RequestParam("file") MultipartFile[] file,
                         @RequestParam(value = "subdirectory", required = false) String subdirectory,
                         RedirectAttributes redirectAttributes){

        String saveDirectory = user.getUsername();

        if(subdirectory!=null && !subdirectory.isEmpty()) {

            saveDirectory = subdirectory;
            redirectAttributes.addAttribute("subdirectory", subdirectory);

        }

        minioService.uploadFile(saveDirectory, file);
        return "redirect:/files";
    }
}
