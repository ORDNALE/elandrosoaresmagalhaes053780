package com.elandroapi.modules.mappers;

import com.elandroapi.modules.dto.request.ArtistaRequest;
import com.elandroapi.modules.dto.response.ArtistaResponse;
import com.elandroapi.modules.entities.Artista;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, uses = {AlbumMapper.class})
public abstract class ArtistaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "albuns", ignore = true) // Albuns são gerenciados no lado do Album
    public abstract Artista toModel(ArtistaRequest request);

    public abstract ArtistaResponse toResponse(Artista model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "albuns", ignore = true) // Albuns são gerenciados no lado do Album
    public abstract void updateModelFromRequest(@MappingTarget Artista model, ArtistaRequest request);

}
