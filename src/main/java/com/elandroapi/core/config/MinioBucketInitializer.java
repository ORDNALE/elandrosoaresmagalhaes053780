package com.elandroapi.core.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MinioBucketInitializer {

    private static final Logger LOG =
            Logger.getLogger(MinioBucketInitializer.class);

    @ConfigProperty(name = "minio.bucket-name")
    String bucketName;

    @Inject
    MinioClient minioClient;

    void onStart(@Observes StartupEvent event) {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                LOG.infof("Bucket MinIO criado: %s", bucketName);
            } else {
                LOG.infof("Bucket MinIO já existe: %s", bucketName);
            }
        } catch (Exception e) {
            LOG.error("Erro ao verificar/criar bucket no MinIO", e);
        }
    }
}