package com.rzh12.notevino.service.impl;

import com.rzh12.notevino.service.S3Service;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.UUID;

@Service
public class S3ServiceImpl implements S3Service {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.base-url}")
    private String baseUrl;

    private S3Client s3Client;

    @PostConstruct
    public void init() {
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())  // 使用預設憑證提供者鏈
                .build();
    }

    @Override
    public String uploadFile(MultipartFile file) {
        // 取得原始檔名的副檔名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        String contentType = file.getContentType();

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String key = "uploads/" + UUID.randomUUID() + extension;  // 使用 UUID 生成唯一文件名

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return baseUrl + key; // 返回 S3 上的文件 URL
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process file", e);
        }
    }
}
