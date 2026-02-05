package com.elandroapi.modules.services;

import com.elandroapi.core.pagination.PageRequest;
import com.elandroapi.core.pagination.Paged;
import com.elandroapi.modules.dto.request.ArtistaFilterRequest;
import com.elandroapi.modules.dto.request.ArtistaRequest;
import com.elandroapi.modules.dto.response.ArtistaResponse;
import com.elandroapi.modules.entities.Artista;
import com.elandroapi.modules.mappers.ArtistaMapper;
import com.elandroapi.modules.repositories.ArtistaRepository;
import com.elandroapi.modules.repositories.UsuarioArtistaFavoritoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class ArtistaService {

    @Inject
    ArtistaRepository repository;

    @Inject
    ArtistaMapper mapper;

    @Inject
    UsuarioArtistaFavoritoRepository favoritoRepository;

    @Inject
    JsonWebToken jwt;

    public Paged<ArtistaResponse> listar(PageRequest pageRequest, ArtistaFilterRequest filter) {
        var query = repository.findByFilters(filter);
        query.page(pageRequest.toPage());

        var content = query.list().stream()
                .map(this::toResponseComFavorito)
                .toList();

        return new Paged<>(query, content);
    }

    public ArtistaResponse buscar(Long id) {
        return toResponseComFavorito(buscarPorId(id));
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

    private ArtistaResponse toResponseComFavorito(Artista artista) {
        ArtistaResponse response = mapper.toResponse(artista);
        if (jwt.getSubject() != null) {
            response.setFavorito(favoritoRepository.isFavorito(jwt.getSubject(), artista.getId()));
        }
        return response;
    }
}
