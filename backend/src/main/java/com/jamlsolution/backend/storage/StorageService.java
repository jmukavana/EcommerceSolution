package com.jamlsolution.backend.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadProductImage(MultipartFile file);
    String uploadAvatar(MultipartFile file);
    void delete(String bucket, String objectName);
}
