package com.elandroapi.modules.services;

import com.elandroapi.modules.dto.filter.ArtistaFilter;
import com.elandroapi.modules.dto.request.ArtistaRequest;
import com.elandroapi.modules.dto.response.ArtistaResponse;
import com.elandroapi.modules.dto.response.Paginacao;
import com.elandroapi.modules.entities.Artista;
import com.elandroapi.modules.mappers.ArtistaMapper;
import com.elandroapi.modules.repositories.ArtistaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class ArtistaService {

    @Inject
    ArtistaRepository repository;

    @Inject
    ArtistaMapper mapper;

    public Paginacao<ArtistaResponse> listar(ArtistaFilter filter) {
        return Paginacao.of(repository.find(filter), filter, mapper::toResponse);
    }

    public ArtistaResponse buscar(Long id) {
        return mapper.toResponse(buscarPorId(id));
    }

    @Transactional
    public ArtistaResponse salvar(ArtistaRequest request) {
        var model = mapper.toModel(request);
        repository.persist(model);
        return mapper.toResponse(model);
    }

    @Transactional
    public void atualizar(Long id, ArtistaRequest request) {
        var model = buscarPorId(id);
        mapper.updateModelFromRequest(model, request);
        repository.persist(model);
    }

    @Transactional
    public void excluir(Long id) {
        repository.deleteById(id);
    }

    private Artista buscarPorId(Long id) {
        return repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException(String.format("Artista %s não encontrado", id)));
    }
}
