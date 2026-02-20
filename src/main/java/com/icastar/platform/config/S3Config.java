package com.icastar.platform.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@ConfigurationProperties(prefix = "icastar.aws.s3")
@Getter
@Setter
@Slf4j
public class S3Config {

    private String accessKey;
    private String secretKey;
    private String region = "ap-south-1";
    private String bucketName = "icastar-uploads";
    private Integer presignedUrlExpiration = 3600; // default 1 hour

    public boolean isConfigured() {
        return accessKey != null && !accessKey.isBlank()
            && secretKey != null && !secretKey.isBlank();
    }

    @PostConstruct
    public void init() {
        if (!isConfigured()) {
            log.warn("⚠️ AWS S3 credentials not configured. Upload API will return error until configured.");
            log.warn("⚠️ Set icastar.aws.s3.access-key and icastar.aws.s3.secret-key in application.yml");
        } else {
            log.info("✅ AWS S3 configured with bucket: {}, region: {}", bucketName, region);
        }
    }

    @Bean
    public S3Client s3Client() {
        if (!isConfigured()) {
            log.warn("S3Client not created - credentials missing");
            return null;
        }

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        if (!isConfigured()) {
            log.warn("S3Presigner not created - credentials missing");
            return null;
        }

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
