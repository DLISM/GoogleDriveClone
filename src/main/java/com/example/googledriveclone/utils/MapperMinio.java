package com.example.googledriveclone.utils;

import io.minio.Result;
import io.minio.messages.Item;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.List;
@Log4j
public class MapperMinio {
    public static List<MinioObject> convert(Iterable<Result<Item>> iterable) {

        List<MinioObject> convertList = new ArrayList<MinioObject>();

        iterable.forEach(itemResult -> {
            try {
                String path = itemResult.get().objectName();

                if(path!=null && !path.isEmpty()) {
                    String name = getName(path);

                    convertList.add(new MinioObject(name, path, itemResult.get().isDir()));
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return convertList;
    }

    private static String getName(String path) {
        var pathArray = path.split("/");
        return pathArray[pathArray.length-1];
    }
}
