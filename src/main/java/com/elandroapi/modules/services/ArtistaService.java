package com.elandroapi.modules.services;

import com.elandroapi.modules.dto.request.ArtistaRequest;
import com.elandroapi.modules.dto.response.ArtistaResponse;
import com.elandroapi.modules.entities.Artista;
import com.elandroapi.modules.mappers.ArtistaMapper;
import com.elandroapi.modules.repositories.ArtistaRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class ArtistaService {

    @Inject
    ArtistaRepository repository;

    @Inject
    ArtistaMapper mapper;

    public List<ArtistaResponse> listar(String nome, String sort) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sort) ? Sort.Direction.Descending : Sort.Direction.Ascending;
        Sort sortOrder = Sort.by("nome").direction(direction);

        PanacheQuery<Artista> query;
        if (nome == null || nome.isBlank()) {
            query = repository.findAll(sortOrder);
        } else {
            query = repository.find("lower(nome) like lower(?1)", sortOrder, "%" + nome + "%");
        }

        return query.stream()
                .map(mapper::toResponse)
                .toList();
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