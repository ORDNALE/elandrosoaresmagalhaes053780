package com.elandroapi.modules.mappers;

import com.elandroapi.modules.dto.response.GeneroResponse;
import com.elandroapi.modules.entities.Genero;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface GeneroMapper {

    GeneroResponse toResponse(Genero entity);
}
