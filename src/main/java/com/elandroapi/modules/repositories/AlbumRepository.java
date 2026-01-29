package com.elandroapi.modules.repositories;

import com.elandroapi.modules.dto.request.AlbumFilterRequest;
import com.elandroapi.modules.entities.Album;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class AlbumRepository implements PanacheRepository<Album> {

    public PanacheQuery<Album> findByFilters(AlbumFilterRequest filter) {
        StringBuilder query = new StringBuilder("SELECT DISTINCT alb FROM Album alb");
        List<String> conditions = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        boolean needsJoin = filter.hasNomeArtista() || filter.hasTipos();

        if (needsJoin) {
            query.append(" JOIN alb.artistas art");
        }

        if (filter.hasNomeArtista()) {
            conditions.add("LOWER(art.nome) LIKE LOWER(:nomeArtista)");
            params.put("nomeArtista", "%" + filter.getNomeArtista() + "%");
        }

        if (filter.hasTipos()) {
            conditions.add("art.tipo IN :tipos");
            params.put("tipos", filter.getTipos());
        }

        if (!conditions.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSort())
                ? Sort.Direction.Descending
                : Sort.Direction.Ascending;

        query.append(" ORDER BY alb.titulo ").append(direction == Sort.Direction.Descending ? "DESC" : "ASC");

        return find(query.toString(), params);
    }
}
