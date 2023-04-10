package com.example.googledriveclone.services.Impl;

import com.example.googledriveclone.exceptions.MinIoFileActionException;
import com.example.googledriveclone.exceptions.MinIoFileNotFoundException;
import com.example.googledriveclone.exceptions.MinIoFileUploadException;
import com.example.googledriveclone.services.MinioService;
import com.example.googledriveclone.utils.MapperMinio;
import com.example.googledriveclone.utils.MinioHelper;
import com.example.googledriveclone.utils.MinioObject;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

@Service
@Log4j
public class MinoServiceImpl implements MinioService {
    @Value("${spring.minio.bucket}")
    private String bucket;

    @Autowired
    private MinioClient minioClient;

    @Override
    public List<MinioObject> folderList(String userFolder) {
        Iterable<Result<Item>> results = minioClient
                .listObjects(
                        ListObjectsArgs.builder()
                                .bucket(bucket)
                                .prefix(userFolder)
                                .recursive(false).build());

        return MapperMinio.convert(results);
    }

    @Override
    public boolean createFolder(String folderName){

        if (folderExist(folderName)) {
            return false;
        }

        String objectName = StringUtils.join(folderName, "/");

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                            .build());
        } catch (Exception e) {
            throw new MinIoFileActionException("File creation failed. Folder name:" + folderName);
        }

        return true;
    }

    @Override
    public boolean folderExist(String folderName){
        return folderList(folderName).iterator().hasNext();
    }

    @Override
    public void deleteFolder(String[] deleteFilesPath) {

        List<DeleteObject> objects = buildDeleteObjects(deleteFilesPath);

        Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                RemoveObjectsArgs.builder().bucket(bucket).objects(objects).build());

        for (Result<DeleteError> result : results) {
            DeleteError error = null;
            try {
                error = result.get();
            } catch (Exception e) {
                throw new MinIoFileActionException("Error in deleting object " + error.objectName() + "; " + error.message());
            }
            log.warn("Error in deleting object " + error.objectName() + "; " + error.message());
        }
    }

    @Override
    public Map<String, MinioObject> search(String userDirectory, String query) {

        Map<String, MinioObject> foundFilesMap = new HashMap<>();
        Iterable<Result<Item>> results = getObjectsRecursive(userDirectory);

        for (Result<Item> itemResult : results) {
            try {
                String objectName = itemResult.get().objectName();
                if (objectName.indexOf(query) != -1) {
                    String path = objectName.substring(0, objectName.indexOf(query) + query.length());

                    if (!foundFilesMap.containsKey(path)) {
                        foundFilesMap.put(
                                path,
                                MinioObject
                                        .builder()
                                        .path(objectName)
                                        .name(MinioHelper.getName(objectName))
                                        .isDirectory(itemResult.get().isDir())
                                        .build()
                        );
                    }
                }
            } catch (Exception e) {
                throw new MinIoFileNotFoundException("File search error");
            }
        }

        return foundFilesMap;
    }

    @Override
    public boolean uploadFile(String userDirectory, MultipartFile[] files) {
        try {
            for (MultipartFile file : files) {

                InputStream in = new ByteArrayInputStream(file.getBytes());
                String fileName = file.getOriginalFilename();
                String objectName = StringUtils.join(userDirectory, "/", fileName);

                minioClient.putObject(
                        PutObjectArgs
                                .builder()
                                .bucket(bucket)
                                .object(objectName)
                                .stream(
                                        in, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );

            }
            return true;

        } catch (Exception e) {
            log.error("Failed upload file");
            throw new MinIoFileUploadException("Failed upload file");
        }
    }

    @Override
    public void renameFile(String filePath, String fileNewName) {
        String newFilePath = MinioHelper.createNewFilePath(filePath, fileNewName);

        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucket)
                            .object(newFilePath)
                            .source(
                                    CopySource.builder()
                                            .bucket(bucket)
                                            .object(filePath)
                                            .build())
                            .build());

            deleteFolder(new String[]{filePath});

        } catch (Exception e) {
            log.error("Failed to rename file");
            throw new MinIoFileActionException("Failed to rename file");
        }
    }

    @Override
    public void renameDirectory(String dirPath, String dirNewName) {
        Iterable<Result<Item>> objectsInDirectory = getObjectsRecursive(dirPath);

        objectsInDirectory.forEach(
                object -> {
                    try {
                        String objectName = object.get().objectName();
                        String objectNewName = MinioHelper.createNewDirectoryPath(dirPath, dirNewName, objectName);

                        minioClient.copyObject(
                                CopyObjectArgs.builder()
                                        .bucket(bucket)
                                        .object(objectNewName)
                                        .source(
                                                CopySource.builder()
                                                        .bucket(bucket)
                                                        .object(objectName)
                                                        .build())
                                        .build());

                        deleteFolder(new String[]{objectName});

                    } catch (Exception e) {
                        log.error("Failed to rename folder");
                        throw new MinIoFileActionException("Failed to rename folder");
                    }
                }
        );
    }

    private Iterable<Result<Item>> getObjectsRecursive(String userDirectory) {
        Iterable<Result<Item>> results = minioClient
                .listObjects(
                        ListObjectsArgs
                                .builder()
                                .bucket(bucket)
                                .prefix(userDirectory)
                                .recursive(true)
                                .build());

        return results;
    }

    /**
     * Рекрсивним методом обходить все папки и составляет список файлов
     *
     * @param deleteFilesPath массив содержающий путь к файлам и папкам для удаления
     * @return List<DeleteObject>
     */
    @NonNull
    private List<DeleteObject> buildDeleteObjects(String[] deleteFilesPath) {
        List<DeleteObject> objects = new LinkedList<>();

        for (String path : deleteFilesPath) {

            Iterable<Result<Item>> results = getObjectsRecursive(path);

            for (Result<Item> itemResult : results) {
                try {
                    objects.add(new DeleteObject(itemResult.get().objectName()));
                } catch (NoSuchElementException e) {
                    log.warn("Empty result for path {}: {}");
                } catch (Exception e) {
                    log.warn("Error creating DeleteObject");
                    throw new MinIoFileActionException("Error creating DeleteObject for path " + path);
                }
            }
        }
        return objects;
    }


}
