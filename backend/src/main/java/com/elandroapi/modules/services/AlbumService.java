package com.elandroapi.modules.services;

import com.elandroapi.core.pagination.PageRequest;
import com.elandroapi.core.pagination.Paged;
import com.elandroapi.modules.dto.request.AlbumFilterRequest;
import com.elandroapi.modules.dto.request.AlbumRequest;
import com.elandroapi.modules.dto.response.AlbumResponse;
import com.elandroapi.modules.entities.Album;
import com.elandroapi.modules.entities.Artista;
import com.elandroapi.modules.entities.Genero;
import com.elandroapi.modules.mappers.AlbumMapper;
import com.elandroapi.modules.repositories.AlbumRepository;
import com.elandroapi.modules.repositories.ArtistaRepository;
import com.elandroapi.modules.repositories.GeneroRepository;
import com.elandroapi.websocket.AlbumNotificationEvent;
import com.elandroapi.websocket.AlbumWebSocket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class AlbumService {

    @Inject
    AlbumRepository repository;

    @Inject
    ArtistaRepository artistaRepository;

    @Inject
    GeneroRepository generoRepository;

    @Inject
    AlbumMapper mapper;

    @Inject
    AlbumWebSocket.Broadcaster broadcaster;

    public Paged<AlbumResponse> listar(PageRequest pageRequest, AlbumFilterRequest filter) {
        var query = repository.findByFilters(filter);
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
        List<Artista> artistas = buscarArtistas(request.getArtistaIds());
        List<Genero> generos = buscarGeneros(request.getGeneroIds());

        var model = mapper.toModel(request);
        model.setArtistas(artistas);
        model.setGeneros(generos);
        repository.persist(model);

        broadcaster.broadcast(AlbumNotificationEvent.novoAlbum(model.getId(), model.getTitulo()));

        return mapper.toResponse(model);
    }

    @Transactional
    public void atualizar(Long id, AlbumRequest request) {
        var model = buscarPorId(id);
        List<Artista> artistas = buscarArtistas(request.getArtistaIds());
        List<Genero> generos = buscarGeneros(request.getGeneroIds());

        mapper.updateModelFromRequest(model, request);
        model.setArtistas(artistas);
        model.setGeneros(generos);
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

    private List<Artista> buscarArtistas(List<Long> artistaIds) {
        if (artistaIds == null || artistaIds.isEmpty()) {
            throw new BadRequestException("artistaIds deve conter ao menos um artista");
        }

        List<Artista> artistas = artistaRepository.list("id IN ?1", artistaIds);

        if (artistas.size() != artistaIds.size()) {
            List<Long> encontrados = artistas.stream().map(Artista::getId).toList();
            List<Long> naoEncontrados = artistaIds.stream()
                    .filter(id -> !encontrados.contains(id))
                    .toList();
            throw new NotFoundException(String.format("Artistas não encontrados: %s", naoEncontrados));
        }

        return artistas;
    }

    private List<Genero> buscarGeneros(List<Long> generoIds) {
        if (generoIds == null || generoIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Genero> generos = generoRepository.list("id IN ?1", generoIds);

        if (generos.size() != generoIds.size()) {
            List<Long> encontrados = generos.stream().map(Genero::getId).toList();
            List<Long> naoEncontrados = generoIds.stream()
                    .filter(id -> !encontrados.contains(id))
                    .toList();
            throw new NotFoundException(String.format("Gêneros não encontrados: %s", naoEncontrados));
        }

        return generos;
    }
}
