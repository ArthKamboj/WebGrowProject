package com.example.webgrow.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

@Service
public class S3Service {

    private final AmazonS3 s3Client;
    private final String bucketName = "webgrowbucket";

    @Autowired
    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadFile(MultipartFile file, String keyName) throws IOException {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        String contentType = file.getContentType();


        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyName, inputStream, metadata);
            s3Client.putObject(putObjectRequest);
        }
    }
    public String getFileUrl(String keyName) {
        return s3Client.getUrl(bucketName, keyName).toString();
    }
}
