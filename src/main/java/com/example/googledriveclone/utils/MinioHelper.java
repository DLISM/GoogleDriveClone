package com.example.googledriveclone.utils;

import lombok.Builder;
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

    @Builder
    public static String createNewDirectoryPath(String dirPath, String dirNewName, String objectName) {
        Path path = Paths.get(dirPath);
        Path parent = path.getParent();
        String newPath = parent.resolve(dirNewName).toString();

        return objectName.replaceFirst(dirPath, newPath+"/");
    }

    public static String getName(String path) {
        return Paths.get(path).getFileName().toString();
    }
}
