package com.example.webgrow.controller;
import com.example.webgrow.Service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
@RestController
@RequestMapping("/s3")
public class FileController {
    private final S3Service s3Service;
    @Autowired
    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam("keyName") String keyName) {
        try {
            s3Service.uploadFile(file, keyName);
            return ResponseEntity.ok("File uploaded successfully!");
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload file!");
        }
    }
    @GetMapping("/file-url/{keyName}")
    public ResponseEntity<String> getFileUrl(@PathVariable String keyName) {
        try {
            String fileUrl = s3Service.getFileUrl(keyName);
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to get file URL: " + e.getMessage());
        }
    }

}
