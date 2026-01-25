package com.app.modules.services;


import com.app.modules.dto.response.AlbumResponse;
import com.app.modules.dto.response.Paginacao;
import com.app.modules.enums.TipoArtista;
import com.app.modules.mappers.AlbumMapper;
import com.app.modules.repositories.AlbumRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class AlbumService {

    @Inject
    AlbumRepository repository;

    @Inject
    AlbumMapper mapper;


    public Paginacao<AlbumResponse> listar(int page, int size, List<TipoArtista> tipos) {
        var query = (tipos == null || tipos.isEmpty()) ? repository.findAll() : repository.findByTipoArtista(tipos);

        query.page(page, size);

        return new Paginacao<>(
                query.list().stream().map(mapper::toResponse).toList(),
                query.pageCount(),
                query.count(),
                size,
                page);
    }
}

