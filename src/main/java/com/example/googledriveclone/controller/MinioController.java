package com.example.googledriveclone.controller;

import com.example.googledriveclone.models.User;
import com.example.googledriveclone.services.MinioService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

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
                               @RequestParam(value = "createFailed", required = false) boolean createFailed) throws Exception{

        model.addAttribute("user", user);
        model.addAttribute("files", minioService.folderList(user.getUsername()));
        model.addAttribute("createSuccess", createSuccess);
        model.addAttribute("createFailed", createFailed);

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
        var filesPathArray = pathList.split(",");

        Arrays.stream(filesPathArray).forEach(i->{
            try {
                if(!i.equals(" "))
                    minioService.deleteFolder(i.trim());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return "redirect:/files";
    }

    @GetMapping("/search")
    public String search(@AuthenticationPrincipal User user, Model model, @RequestParam("query") String query) throws Exception {

        model.addAttribute("user", user);
        model.addAttribute("files", minioService.search("Abu/old files/"));

        log.info(query);

        return "search";
    }
}
