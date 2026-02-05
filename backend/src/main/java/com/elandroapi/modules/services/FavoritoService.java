package com.elandroapi.modules.services;

import com.elandroapi.modules.entities.UsuarioAlbumFavorito;
import com.elandroapi.modules.entities.UsuarioArtistaFavorito;
import com.elandroapi.modules.repositories.AlbumRepository;
import com.elandroapi.modules.repositories.ArtistaRepository;
import com.elandroapi.modules.repositories.UsuarioAlbumFavoritoRepository;
import com.elandroapi.modules.repositories.UsuarioArtistaFavoritoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class FavoritoService {

    @Inject
    UsuarioArtistaFavoritoRepository artistaFavoritoRepository;

    @Inject
    UsuarioAlbumFavoritoRepository albumFavoritoRepository;

    @Inject
    ArtistaRepository artistaRepository;

    @Inject
    AlbumRepository albumRepository;

    @Transactional
    public void favoritarArtista(String usuarioId, Long artistaId) {
        if (artistaRepository.findByIdOptional(artistaId).isEmpty()) {
            throw new NotFoundException("Artista não encontrado");
        }

        if (artistaFavoritoRepository.isFavorito(usuarioId, artistaId)) {
            throw new WebApplicationException("Artista já favoritado", 409);
        }

        UsuarioArtistaFavorito favorito = new UsuarioArtistaFavorito();
        favorito.setUsuarioId(usuarioId);
        favorito.setArtistaId(artistaId);
        artistaFavoritoRepository.persist(favorito);
    }

    @Transactional
    public void desfavoritarArtista(String usuarioId, Long artistaId) {
        if (!artistaFavoritoRepository.isFavorito(usuarioId, artistaId)) {
            throw new NotFoundException("Favorito não encontrado");
        }
        artistaFavoritoRepository.delete(usuarioId, artistaId);
    }

    @Transactional
    public void favoritarAlbum(String usuarioId, Long albumId) {
        if (albumRepository.findByIdOptional(albumId).isEmpty()) {
            throw new NotFoundException("Álbum não encontrado");
        }

        if (albumFavoritoRepository.isFavorito(usuarioId, albumId)) {
            throw new WebApplicationException("Álbum já favoritado", 409);
        }

        UsuarioAlbumFavorito favorito = new UsuarioAlbumFavorito();
        favorito.setUsuarioId(usuarioId);
        favorito.setAlbumId(albumId);
        albumFavoritoRepository.persist(favorito);
    }

    @Transactional
    public void desfavoritarAlbum(String usuarioId, Long albumId) {
        if (!albumFavoritoRepository.isFavorito(usuarioId, albumId)) {
            throw new NotFoundException("Favorito não encontrado");
        }
        albumFavoritoRepository.delete(usuarioId, albumId);
    }
}
