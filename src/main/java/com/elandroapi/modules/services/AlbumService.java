package com.elandroapi.modules.services;


import com.elandroapi.modules.dto.response.AlbumResponse;
import com.elandroapi.modules.dto.response.Paginacao;
import com.elandroapi.modules.enums.TipoArtista;
import com.elandroapi.modules.mappers.AlbumMapper;
import com.elandroapi.modules.repositories.AlbumRepository;
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

