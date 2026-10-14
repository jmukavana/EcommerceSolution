package com.jamlsolution.backend.storage.imp;

import com.jamlsolution.backend.storage.StorageService;
import io.minio.*;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageServiceImpl implements StorageService {

    private final MinioClient minioClient;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.bucket-products}")
    private String productsBucket;

    @Value("${minio.bucket-avatars}")
    private String avatarsBucket;

    @Override
    public String uploadProductImage(MultipartFile file) {
        return upload(file, productsBucket);
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        return upload(file, avatarsBucket);
    }

    @Override
    public void delete(String bucket, String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            log.warn("Failed to delete object {}/{}: {}", bucket, objectName, e.getMessage());
        }
    }

    private String upload(MultipartFile file, String bucket) {
        try {
            ensureBucketExists(bucket);
            String objectName = UUID.randomUUID() + "-" + sanitize(file.getOriginalFilename());
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            return endpoint + "/" + bucket + "/" + objectName;
        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
    }

    private void ensureBucketExists(String bucket) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            // Make bucket public-read
            String policy = """
                    {"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"AWS":["*"]},
                    "Action":["s3:GetObject"],"Resource":["arn:aws:s3:::%s/*"]}]}
                    """.formatted(bucket);
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(policy).build());
        }
    }

    private String sanitize(String filename) {
        if (filename == null) return "file";
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
