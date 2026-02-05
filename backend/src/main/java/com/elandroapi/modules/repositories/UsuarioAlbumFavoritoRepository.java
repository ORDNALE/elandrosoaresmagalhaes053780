package com.elandroapi.modules.repositories;

import com.elandroapi.modules.entities.UsuarioAlbumFavorito;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UsuarioAlbumFavoritoRepository implements PanacheRepository<UsuarioAlbumFavorito> {

    public boolean isFavorito(String usuarioId, Long albumId) {
        return count("usuarioId = ?1 and albumId = ?2", usuarioId, albumId) > 0;
    }

    public void delete(String usuarioId, Long albumId) {
        delete("usuarioId = ?1 and albumId = ?2", usuarioId, albumId);
    }
}
