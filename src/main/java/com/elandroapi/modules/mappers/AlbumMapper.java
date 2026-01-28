package com.elandroapi.modules.mappers;

import com.elandroapi.modules.dto.request.AlbumRequest;
import com.elandroapi.modules.dto.response.AlbumResponse;
import com.elandroapi.modules.dto.response.ArtistaResumoResponse;
import com.elandroapi.modules.entities.Album;
import com.elandroapi.modules.entities.Artista;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, uses = {CapaAlbumMapper.class}, unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    Album toModel(AlbumRequest request);

    AlbumResponse toResponse(Album model);

    ArtistaResumoResponse toArtistaResumo(Artista artista);

    void updateModelFromRequest(@MappingTarget Album model, AlbumRequest request);
}
