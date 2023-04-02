package com.example.googledriveclone.controller;

import com.example.googledriveclone.models.User;
import com.example.googledriveclone.services.MinioService;
import com.example.googledriveclone.utils.MinioHelper;
import com.example.googledriveclone.utils.UserDirectoryTree;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@RequestMapping("/files")
@Log4j
public class MinioController {

    @Autowired
    private MinioService minioService;

    @GetMapping("")
    public String getUserFilesView(@AuthenticationPrincipal User user,
                                   Model model,
                                   @RequestParam(value = "createSuccess", required = false) boolean createSuccess,
                                   @RequestParam(value = "createFailed", required = false) boolean createFailed,
                                   @RequestParam(value = "", required = false) String subdirectory){

        String directory = MinioHelper.getDirectory(user, subdirectory);
        List<UserDirectoryTree> currentDirectory = MinioHelper.createDirectoryTree(subdirectory);

        model.addAttribute("files", minioService.folderList(directory));
        model.addAttribute("user", user);
        model.addAttribute("directory", currentDirectory);
        model.addAttribute("createSuccess", createSuccess);
        model.addAttribute("createFailed", createFailed);

        return "files";
    }

    @PostMapping("/create")
    public RedirectView createFolderAction(@AuthenticationPrincipal User user,
                                           @ModelAttribute("folderName") String folderName,
                                           @RequestParam(value = "subdirectory", required = false) String subdirectory,
                                           RedirectAttributes redirectAttributes) {

        String newFolder = StringUtils.join(user.getUsername(), "/");

        if(StringUtils.isNotBlank(subdirectory)) {
            newFolder=StringUtils.join(subdirectory, folderName);
            redirectAttributes.addAttribute("subdirectory", subdirectory);
        }else {
            newFolder += folderName;
        }

        boolean creatFolder = minioService.createFolder(newFolder);

        if(creatFolder) {
            redirectAttributes.addAttribute("createSuccess", true);
        }
        else {
            redirectAttributes.addAttribute("createFailed", true);
        }

        return new RedirectView("/files");
    }

    @PostMapping("/delete")
    public String delete(
            @RequestParam("files") String pathList,
            @RequestParam(value = "subdirectory", required = false) String subdirectory,
            RedirectAttributes redirectAttributes){

        if(StringUtils.isNotBlank(subdirectory)) {
            redirectAttributes.addAttribute("subdirectory", subdirectory);
        }

        String[] deleteFilesPath = MinioHelper.getDeleteFilesPath(pathList);

        minioService.deleteFolder(deleteFilesPath);
        redirectAttributes.addAttribute("deleteSuccess", true);

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

        if(StringUtils.isNotBlank(subdirectory)) {
            saveDirectory = subdirectory;
            redirectAttributes.addAttribute("subdirectory", subdirectory);
        }

        minioService.uploadFile(saveDirectory, file);
        return "redirect:/files";
    }

    @PostMapping("/rename")
    public String rename(
            @RequestParam("filePath") String filePath,
            @RequestParam("fileName") String fileName,
            @RequestParam("type") String objectType,
            @RequestParam(value = "subdirectory", required = false) String subdirectory,
            RedirectAttributes redirectAttributes
    ){

        if(StringUtils.isNotBlank(subdirectory)) {
            redirectAttributes.addAttribute("subdirectory", subdirectory);
        }

        if(objectType.equals("file")) {
            minioService.renameFile(filePath, fileName);
        }else {
            minioService.renameDirectory(filePath, fileName);
        }

        return "redirect:/files";
    }


}
