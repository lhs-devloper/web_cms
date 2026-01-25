package com.lhsdev.cmsproject.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File is empty"));
        }

        try {
            // Ensure directory exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 1. Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String savedFilename = UUID.randomUUID().toString() + extension;

            // 2. Save to directory
            Path path = uploadPath.resolve(savedFilename);
            Files.copy(file.getInputStream(), path);

            // 3. Return absolute URL
            String baseUrl = request.getScheme() + "://" + request.getServerName();
            int port = request.getServerPort();
            if ((request.getScheme().equals("http") && port != 80)
                    || (request.getScheme().equals("https") && port != 443)) {
                baseUrl += ":" + port;
            }
            String fileUrl = baseUrl + "/uploads/" + savedFilename;
            log.info("File uploaded successfully: {}", fileUrl);

            return ResponseEntity.ok(Map.of("url", fileUrl));

        } catch (IOException e) {
            log.error("Failed to upload file", e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Upload failed: " + e.getMessage()));
        }
    }
}