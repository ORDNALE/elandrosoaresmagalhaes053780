package com.elandroapi.modules.services;

import com.elandroapi.modules.dto.filter.AlbumFilter;
import com.elandroapi.modules.dto.request.AlbumRequest;
import com.elandroapi.modules.dto.response.AlbumResponse;
import com.elandroapi.modules.dto.response.Paginacao;
import com.elandroapi.modules.entities.Album;
import com.elandroapi.modules.entities.Artista;
import com.elandroapi.modules.mappers.AlbumMapper;
import com.elandroapi.modules.repositories.AlbumRepository;
import com.elandroapi.modules.repositories.ArtistaRepository;
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
    ArtistaRepository artistaRepository;

    @Inject
    AlbumMapper mapper;

    public Paginacao<AlbumResponse> listar(AlbumFilter filter) {
        return Paginacao.of(repository.find(filter), filter, mapper::toResponse);
    }

    public AlbumResponse buscarPorId(Long id) {
        return repository.findByIdOptional(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Álbum não encontrado"));
    }

    @Transactional
    public AlbumResponse salvar(AlbumRequest request) {
        Album album = new Album();
        album.setTitulo(request.getTitulo());
        List<Artista> artistas = artistaRepository.list("id in ?1", request.getArtistaIds());
        if (artistas.size() != request.getArtistaIds().size()) {
            throw new NotFoundException("Um ou mais artistas não foram encontrados.");
        }
        album.setArtistas(artistas);
        repository.persist(album);
        return mapper.toResponse(album);
    }

    @Transactional
    public AlbumResponse atualizar(Long id, AlbumRequest request) {
        Album album = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Álbum não encontrado"));

        album.setTitulo(request.getTitulo());
        List<Artista> artistas = artistaRepository.list("id in ?1", request.getArtistaIds());
        if (artistas.size() != request.getArtistaIds().size()) {
            throw new NotFoundException("Um ou mais artistas não foram encontrados.");
        }
        album.setArtistas(artistas);
        repository.persist(album);
        return mapper.toResponse(album);
    }
}
