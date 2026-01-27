package com.elandroapi.modules.services;

import com.elandroapi.core.pagination.PageRequest;
import com.elandroapi.core.pagination.Paged;
import com.elandroapi.modules.dto.request.AlbumRequest;
import com.elandroapi.modules.dto.response.AlbumResponse;
import com.elandroapi.modules.entities.Album;
import com.elandroapi.modules.enums.TipoArtista;
import com.elandroapi.modules.mappers.AlbumMapper;
import com.elandroapi.modules.repositories.AlbumRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class AlbumService {

    @Inject
    AlbumRepository repository;

    @Inject
    AlbumMapper mapper;

    public Paged<AlbumResponse> listar(PageRequest pageRequest, List<TipoArtista> tipos) {
        var query = (tipos == null || tipos.isEmpty())
                ? repository.findAll()
                : repository.findByTipoArtista(tipos);

        query.page(pageRequest.toPage());

        var content = query.list().stream()
                .map(mapper::toResponse)
                .toList();

        return new Paged<>(query, content);
    }

    public AlbumResponse buscar(Long id) {
        return mapper.toResponse(buscarPorId(id));
    }

    @Transactional
    public AlbumResponse salvar(AlbumRequest request) {
        var model = mapper.toModel(request);
        repository.persist(model);
        return mapper.toResponse(model);
    }

    @Transactional
    public void atualizar(Long id, AlbumRequest request) {
        var model = buscarPorId(id);
        mapper.updateModelFromRequest(model, request);
        repository.persist(model);
    }

    @Transactional
    public void excluir(Long id) {
        repository.deleteById(id);
    }

    private Album buscarPorId(Long id) {
        return repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException(String.format("Álbum %s não encontrado", id)));
    }
}

