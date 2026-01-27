package com.elandroapi.modules.mappers;

import com.elandroapi.modules.dto.request.AlbumRequest;
import com.elandroapi.modules.dto.response.AlbumResponse;
import com.elandroapi.modules.dto.response.ArtistaResumoResponse;
import com.elandroapi.modules.entities.Album;
import com.elandroapi.modules.entities.Artista;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, uses = {CapaAlbumMapper.class})
public interface AlbumMapper {

    Album toModel(AlbumRequest request);

    AlbumResponse toResponse(Album model);

    ArtistaResumoResponse toArtistaResumo(Artista artista);

    void updateModelFromRequest(@MappingTarget Album model, AlbumRequest request);
}
