package com.example.googledriveclone.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Minio {
    @Value("${spring.minio.url}")
    private String url;
    @Value("${spring.minio.access-key}")
    private String accessKey;
    @Value("${spring.minio.secret-key}")
    private String secretKey;
    @Value("${spring.minio.port}")
    private int port;
    @Bean
    public MinioClient getMinioClient(){
        return  MinioClient.builder()
                .endpoint(url, port, false)
                .credentials(accessKey, secretKey)
                .build();
    }

}
