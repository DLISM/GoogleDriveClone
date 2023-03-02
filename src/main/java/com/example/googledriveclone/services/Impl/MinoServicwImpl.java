package com.example.googledriveclone.services.Impl;

import com.example.googledriveclone.models.User;
import com.example.googledriveclone.services.MinioService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j
public class MinoServicwImpl implements MinioService {
    @Value("${spring.minio.bucket}")
    private String bucket;

    @Autowired
    private MinioClient minioClient;

    @Override
    public boolean isBucketExist() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient
                .bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
    }

    @Override
    public Iterable<Result<Item>> folderList(String userFolder) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //TODO возвращать только имю файлов и папок
        Iterable<Result<Item>> results = minioClient
                    .listObjects(
                        ListObjectsArgs.builder()
                                .bucket(bucket)
                                .prefix(userFolder)
                                .recursive(false).build());

        return results;
    }

    @Override
    public boolean createFolder(String folderName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        if(folderExist(folderName)){
            return false;
        }

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(folderName+"/")
                        .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                        .build());

        return true;
    }

    @Override
    public boolean folderExist(String folderName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return folderList(folderName).iterator().hasNext();
    }

    @Override
    public void deleteFolder(String folderName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        //TODO удаления всех объектов сразу
        var folderList = objectListRecursive(folderName);

        folderList.forEach(itemResult -> {
            try {
                minioClient.removeObject(
                        RemoveObjectArgs
                                .builder()
                                .bucket(bucket)
                                .object(itemResult.get().objectName())
                                .build());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Override
    public Map<String, String> search(String userDirectory, String query) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //TODO  проверка на папку
        var foundFilesMap = new HashMap<String, String>();
        var results = objectListRecursive(userDirectory);

        for (Result<Item> itemResult : results) {
            var objectName = itemResult.get().objectName();

            if (objectName.indexOf(query)!=-1){
                var path = objectName.substring(0, objectName.indexOf(query)+query.length());

                if(!foundFilesMap.containsKey(path))
                    foundFilesMap.put(path, objectName);

            }
        }

        return foundFilesMap;
    }

    private Iterable<Result<Item>> objectListRecursive(String userDirectory) {
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

    @Override
    public boolean uploadFile(MultipartFile[] files) {
        try {
            for (MultipartFile file : files ) {
                InputStream in = new ByteArrayInputStream(file.getBytes());
                String fileName = file.getOriginalFilename();
                minioClient.putObject(
                        PutObjectArgs
                                .builder()
                                .bucket(bucket)
                                .object(fileName)
                                .stream(
                                        in,  file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
                log.info(file.getOriginalFilename());
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
