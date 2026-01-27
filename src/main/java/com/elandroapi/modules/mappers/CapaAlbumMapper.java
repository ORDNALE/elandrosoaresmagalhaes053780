package com.elandroapi.modules.mappers;


import com.elandroapi.modules.dto.response.CapaAlbumResponse;
import com.elandroapi.modules.entities.CapaAlbum;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import jakarta.inject.Inject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public abstract class CapaAlbumMapper {

    static final int TRINTA_MINUTOS = 60 * 30;

    @Inject
    MinioClient minioClient;

    @Mapping(target = "id", source = "id")
    @Mapping(target = "url", source = ".", qualifiedByName = "getUrl")
    public abstract CapaAlbumResponse toResponse(CapaAlbum capaAlbum);

    @Named("getUrl")
    protected String getUrl(CapaAlbum capaAlbum) {
        if (capaAlbum == null || capaAlbum.getBucket() == null || capaAlbum.getHash() == null) {
            return null;
        }
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(capaAlbum.getBucket())
                            .object(capaAlbum.getHash())
                            .expiry(TRINTA_MINUTOS)
                            .build());
        } catch (Exception e) {
            return null;
        }
    }
}