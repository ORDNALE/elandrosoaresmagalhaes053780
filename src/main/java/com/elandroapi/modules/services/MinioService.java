package com.elandroapi.modules.services;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.InputStream;
import java.time.Duration;

@ApplicationScoped
public class MinioService {

    @Inject
    MinioClient minio;

    @ConfigProperty(name = "quarkus.minio.bucket")
    String bucket;

    private static final Duration URL_EXPIRATION = Duration.ofMinutes(30);


    public void enviar(String filename, InputStream input) throws Exception {
        minio.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(filename)
                        .stream(input, -1, 10485760)
                        .build()
        );
    }

    public String gerarUrl(String filename) throws Exception {
        return minio.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucket)
                        .object(filename)
                        .expiry((int)URL_EXPIRATION.getSeconds())
                        .build()
        );
    }

    public void remove(String bucket, String objectName) throws Exception {
        minio.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build()
        );
    }

}

