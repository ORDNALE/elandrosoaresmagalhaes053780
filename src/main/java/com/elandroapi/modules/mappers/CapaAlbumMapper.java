package com.elandroapi.modules.mappers;

import com.elandroapi.modules.dto.response.CapaAlbumResponse;
import com.elandroapi.modules.entities.CapaAlbum;
import com.elandroapi.modules.services.MinioService;
import jakarta.inject.Inject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public abstract class CapaAlbumMapper {

    @Inject
    MinioService minioService;

    @Mapping(target = "id", source = "id")
    @Mapping(target = "url", source = "capaAlbum", qualifiedByName = "getUrl")
    public abstract CapaAlbumResponse toResponse(CapaAlbum capaAlbum);

    @Named("getUrl")
    protected String getUrl(CapaAlbum capaAlbum) {
        if (capaAlbum == null) {
            return null;
        }
        try {
            return minioService.gerarUrl(capaAlbum);
        } catch (Exception e) {
            // Em um cenário real, logar o erro é uma boa prática.
            return null;
        }
    }
}
