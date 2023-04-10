package com.example.googledriveclone.services;

import com.example.googledriveclone.utils.MinioObject;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;


public interface MinioService {

    List<MinioObject> folderList(String userFolder);

    Map<String, MinioObject> search(String userDirectory, String userFolder);

    boolean createFolder(String folderName);

    boolean folderExist(String folderName);

    void deleteFolder(String[] folderName);

    boolean uploadFile(String userDirectory, MultipartFile[] file);

    void renameFile(String filePath, String fileNewName);

    void renameDirectory(String filePath, String fileName);
}
