package com.example.googledriveclone.services;

import com.example.googledriveclone.utils.MinioObject;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;


public interface MinioService {

    List<MinioObject> folderList(String userFolder);

    Map<String, MinioObject> search(String userDirectory, String userFolder);

    boolean createFolder(String folderName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    boolean folderExist(String folderName);

    void deleteFolder(String[] folderName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    boolean uploadFile(String userDirectory, MultipartFile[] file);

    void renameFile(String filePath, String fileNewName);

    void renameDirectory(String filePath, String fileName);
}
