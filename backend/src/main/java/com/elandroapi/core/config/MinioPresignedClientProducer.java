package com.elandroapi.core.config;

import io.minio.MinioClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class MinioPresignedClientProducer {

    @ConfigProperty(name = "minio.external-url")
    String externalUrl;

    @ConfigProperty(name = "quarkus.minio.access-key")
    String accessKey;

    @ConfigProperty(name = "quarkus.minio.secret-key")
    String secretKey;

    @Produces
    @Presigned
    MinioClient presignedClient() {
        return MinioClient.builder()
                .endpoint(externalUrl)
                .credentials(accessKey, secretKey)
                .region("us-east-1")
                .build();
    }
}
