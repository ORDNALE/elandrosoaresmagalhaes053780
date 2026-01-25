package com.app.modules.mappers;

import com.app.modules.dto.request.AlbumRequest;
import com.app.modules.dto.response.AlbumResponse;
import com.app.modules.entities.Album;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, uses = { CapaAlbumMapper.class })
public interface AlbumMapper {

    Album toModel(AlbumRequest request);

    AlbumResponse toResponse(Album model);

    void updateModelFromRequest(@MappingTarget Album model, AlbumRequest request);
}