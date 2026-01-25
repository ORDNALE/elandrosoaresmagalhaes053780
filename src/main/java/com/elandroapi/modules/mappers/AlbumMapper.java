package com.elandroapi.modules.mappers;

import com.elandroapi.modules.dto.request.AlbumRequest;
import com.elandroapi.modules.dto.response.AlbumResponse;
import com.elandroapi.modules.entities.Album;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, uses = { CapaAlbumMapper.class })
public interface AlbumMapper {

    Album toModel(AlbumRequest request);

    AlbumResponse toResponse(Album model);

    void updateModelFromRequest(@MappingTarget Album model, AlbumRequest request);
}