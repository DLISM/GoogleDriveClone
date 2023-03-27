package com.example.googledriveclone.utils;

import lombok.extern.log4j.Log4j;

import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j
public final class MinioHelper {
    public static String createNewFilePath(String filePath, String fileNewName){

        Path path = Paths.get(filePath);
        Path parent = path.getParent();

        return parent.resolve(fileNewName).toString();
    }
}
