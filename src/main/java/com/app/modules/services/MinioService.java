package com.app.modules.services;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.InputStream;

@ApplicationScoped
public class MinioService {

    @Inject
    MinioClient minio;

    @ConfigProperty(name = "quarkus.minio.bucket")
    String bucket;

    public void enviar(String filename, InputStream input) throws Exception {
        minio.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(filename)
                        .stream(input, -1, 10485760)
                        .build()
        );
    }
}

