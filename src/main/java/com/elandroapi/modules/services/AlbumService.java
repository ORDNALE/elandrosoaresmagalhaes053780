package com.elandroapi.modules.services;

import com.elandroapi.modules.dto.filter.AlbumFilter;
import com.elandroapi.modules.dto.response.AlbumResponse;
import com.elandroapi.modules.dto.response.Paginacao;
import com.elandroapi.modules.mappers.AlbumMapper;
import com.elandroapi.modules.repositories.AlbumRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AlbumService {

    @Inject
    AlbumRepository repository;

    @Inject
    AlbumMapper mapper;

    public Paginacao<AlbumResponse> listar(AlbumFilter filter) {
        return Paginacao.of(repository.find(filter), filter, mapper::toResponse);
    }
}
