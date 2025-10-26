package org.neiasalgados.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.IOException;

@Service
public class S3Service {
    private final S3Client s3Client;
    @Value("${aws.s3.bucket}")
    private String bucketName;
    @Value("${aws.cloudfront.url}")
    private String cloudfrontUrl;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String sanitizedFileName = sanitizeFileName(file.getOriginalFilename());
        String fileKey = System.currentTimeMillis() + "-" + sanitizedFileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(this.bucketName)
                .key(fileKey)
                .contentType(file.getContentType())
                .build();

        this.s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        return this.cloudfrontUrl + fileKey;
    }

    public void deleteFile(String fileKey) {
        if (fileKey.contains(this.cloudfrontUrl)) {
            fileKey = fileKey.replace(this.cloudfrontUrl, "");
        }

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(this.bucketName)
                .key(fileKey)
                .build();

        this.s3Client.deleteObject(deleteRequest);
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null) return null;
        return fileName.replaceAll("\\s+", "-")
                .replaceAll("[^a-zA-Z0-9\\-_.]", "")
                .toLowerCase();
    }
}
