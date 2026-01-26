package com.elandroapi.modules.repositories;

import com.elandroapi.modules.dto.filter.AlbumFilter;
import com.elandroapi.modules.entities.Album;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class AlbumRepository implements PanacheRepository<Album> {

    public PanacheQuery<Album> find(AlbumFilter filter) {
        StringBuilder query = new StringBuilder("SELECT DISTINCT a FROM Album a LEFT JOIN a.artistas artista WHERE 1=1 ");
        Map<String, Object> params = new HashMap<>();

        if (filter.getTitulo() != null && !filter.getTitulo().isBlank()) {
            query.append("AND lower(a.titulo) LIKE lower(:titulo) ");
            params.put("titulo", "%" + filter.getTitulo() + "%");
        }

        if (filter.getTipos() != null && !filter.getTipos().isEmpty()) {
            query.append("AND artista.tipo IN (:tipos) ");
            params.put("tipos", filter.getTipos());
        }

        return find(query.toString(), params);
    }
}
