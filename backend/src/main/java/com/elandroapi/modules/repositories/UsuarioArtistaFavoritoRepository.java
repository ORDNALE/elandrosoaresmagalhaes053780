package com.elandroapi.modules.repositories;

import com.elandroapi.modules.entities.UsuarioArtistaFavorito;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UsuarioArtistaFavoritoRepository implements PanacheRepository<UsuarioArtistaFavorito> {

    public boolean isFavorito(String usuarioId, Long artistaId) {
        return count("usuarioId = ?1 and artistaId = ?2", usuarioId, artistaId) > 0;
    }

    public void delete(String usuarioId, Long artistaId) {
        delete("usuarioId = ?1 and artistaId = ?2", usuarioId, artistaId);
    }
}
