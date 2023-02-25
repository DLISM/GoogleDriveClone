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
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j
public class MinoServicwImpl implements MinioService {
    @Value("${spring.minio.bucket}")
    private String bucket;

    @Autowired
    private MinioClient minioClient;

    @Override
    public boolean isBucketExist() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
    }

    @Override
    public Iterable<Result<Item>> objectList(String userFolder) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // Lists objects information recursively.
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucket).prefix(userFolder+"/").recursive(false).build());

        return results;
    }

    @Override
    public boolean createFolder(String folderName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        if(objectList(folderName).iterator().hasNext()){
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


}
