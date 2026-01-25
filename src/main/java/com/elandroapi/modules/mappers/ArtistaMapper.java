package com.app.modules.mappers;

import com.app.modules.dto.request.ArtistaRequest;
import com.app.modules.dto.response.ArtistaResponse;
import com.app.modules.entities.Album;
import com.app.modules.entities.Artista;
import com.app.modules.repositories.AlbumRepository;
import jakarta.inject.Inject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public abstract class ArtistaMapper {

    @Inject
    AlbumRepository albumRepository;

    @Mapping(target = "albuns", source = "albuns", qualifiedByName = "mapAlbuns")
    @Mapping(target = "id", ignore = true)
    public abstract Artista toModel(ArtistaRequest request);

    public abstract ArtistaResponse toResponse(Artista model);

    @Mapping(target = "albuns", source = "albuns", qualifiedByName = "mapAlbuns")
    @Mapping(target = "id", ignore = true)
    public abstract void updateModelFromRequest(@MappingTarget Artista model, ArtistaRequest request);

    @Named("mapAlbuns")
    protected List<Album> mapAlbuns(List<ArtistaRequest.AlbumId> albumIds) {
        if (albumIds == null || albumIds.isEmpty()) {
            return Collections.emptyList();
        }
        return albumIds.stream()
                .map(albumId -> albumRepository.findById(albumId.getId()))
                .filter(Objects::nonNull)
                .toList();
    }
}