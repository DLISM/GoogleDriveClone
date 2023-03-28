package com.example.googledriveclone.utils;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MinioObject {
    private String name;
    private String path;
    private boolean isDirectory;
}
