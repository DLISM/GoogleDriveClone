package com.example.googledriveclone.utils;

import com.example.googledriveclone.models.User;
import lombok.Builder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Log4j
public final class MinioHelper {
    public static String createNewFilePath(String filePath, String fileNewName){
        Path path = Paths.get(filePath);
        Path parent = path.getParent();

        return parent.resolve(fileNewName).toString();
    }

    public static String createNewDirectoryPath(String dirPath, String dirNewName, String objectName) {
        Path path = Paths.get(dirPath);
        Path parent = path.getParent();
        String newPath = parent.resolve(dirNewName).toString();

        return objectName.replaceFirst(
                dirPath,
                StringUtils.join(newPath,"/")
        );
    }

    public static String getName(String path) {
        return Paths.get(path).getFileName().toString();
    }

    public static String getDirectory(User user, String subdirectory) {
        String directory = StringUtils.join(user.getUsername(), "/");

        if (StringUtils.isNotBlank(subdirectory)) {
            directory = subdirectory;
        }
        return directory;
    }

    public static List<UserDirectoryTree> createDirectoryTree(String subdirectory){
        List<UserDirectoryTree> result = new ArrayList<>();

        if(StringUtils.isNotBlank(subdirectory)){

            String[] dirArray =  subdirectory.split("/");
            StringBuilder path = new StringBuilder("/");

            for (int i = 0; i< dirArray.length; i++){
                path.append(dirArray[i]+"/");
                result.add(new UserDirectoryTree(dirArray[i], path.toString()));
            }
        }

        return result;
    }
}
