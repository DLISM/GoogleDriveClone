package com.example.googledriveclone.services.Impl;

import com.example.googledriveclone.services.MinioService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
            Iterable<Result<Item>> results = minioClient
                    .listObjects(
                        ListObjectsArgs.builder()
                                .bucket(bucket)
                                .prefix(userFolder+"/")
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
                        .object(folderName+"/test.txt")
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
       var folderList = minioClient
               .listObjects(
                    ListObjectsArgs
                            .builder()
                            .bucket(bucket)
                            .prefix(folderName)
                            .recursive(true)
                            .build());

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
    public Iterable<Result<Item>> search(String query) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<Item>> results = minioClient
                .listObjects(
                    ListObjectsArgs
                            .builder()
                            .bucket(bucket)
                            .prefix("test.txt")
                            .recursive(true)
                            .build());
        return results;
    }
}
