package com.example.googledriveclone.utils;

import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j
public class MapperMinio {
    public static List<MinioObject> convert(Iterable<Result<Item>> iterable) {

        try {
            return StreamSupport.stream(iterable.spliterator(), false)
                    .map(result -> {
                        try {
                            String path = result.get().objectName();
                            if (path != null && !path.isEmpty()) {
                                String name = MinioHelper.getName(path);

                                return MinioObject
                                        .builder()
                                        .name(name)
                                        .path(path)
                                        .isDirectory(result.get().isDir())
                                        .build();
                            }
                            return null;
                        } catch (RuntimeException | ErrorResponseException | InsufficientDataException |
                                 InternalException | InvalidKeyException | InvalidResponseException | IOException |
                                 NoSuchAlgorithmException | ServerException | XmlParserException e) {
                            throw new RuntimeException("Error converting Minio objects", e);
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (RuntimeException e) {
            throw new RuntimeException("Error converting Minio objects", e);
        }

    }



}
