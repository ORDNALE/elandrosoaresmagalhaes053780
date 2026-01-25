package com.elandroapi.modules.repositories;

import com.elandroapi.modules.entities.Album;
import com.elandroapi.modules.enums.TipoArtista;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class AlbumRepository implements PanacheRepository<Album> {

    public PanacheQuery<Album> findByTipoArtista(List<TipoArtista> tipos) {
        return find("select distinct alb from Album alb join alb.artistas art where art.tipo in (?1)", tipos);
    }
}