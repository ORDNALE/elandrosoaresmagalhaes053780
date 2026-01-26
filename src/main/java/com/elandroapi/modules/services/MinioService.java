package com.elandroapi.modules.services;

import com.elandroapi.modules.entities.CapaAlbum;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.InputStream;
import java.time.Duration;

@ApplicationScoped
public class MinioService {

    @Inject
    MinioClient minio;

    private static final Duration URL_EXPIRATION = Duration.ofMinutes(30);

    public void enviar(String bucket, String filename, InputStream input, long size, String contentType) throws Exception {
        minio.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(filename)
                        .stream(input, size, -1)
                        .contentType(contentType)
                        .build()
        );
    }

    public String gerarUrl(CapaAlbum capa) throws Exception {
        return minio.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(capa.getBucket())
                        .object(capa.getHash())
                        .expiry((int)URL_EXPIRATION.getSeconds())
                        .build()
        );
    }

    public void remove(CapaAlbum capa) throws Exception {
        minio.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(capa.getBucket())
                        .object(capa.getHash())
                        .build()
        );
    }
}
