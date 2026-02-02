package com.elandroapi.modules.repositories;

import com.elandroapi.modules.dto.request.ArtistaFilterRequest;
import com.elandroapi.modules.entities.Artista;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ArtistaRepository implements PanacheRepository<Artista> {

    public PanacheQuery<Artista> findByFilters(ArtistaFilterRequest filter) {
        List<String> conditions = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (filter.hasNome()) {
            conditions.add("LOWER(nome) LIKE LOWER(:nome)");
            params.put("nome", "%" + filter.getNome() + "%");
        }

        if (filter.hasTipo()) {
            conditions.add("tipo = :tipo");
            params.put("tipo", filter.getTipo());
        }

        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSort())
                ? Sort.Direction.Descending
                : Sort.Direction.Ascending;
        Sort sortOrder = Sort.by("nome").direction(direction);

        if (conditions.isEmpty()) {
            return findAll(sortOrder);
        }

        String query = String.join(" AND ", conditions);
        return find(query, sortOrder, params);
    }
}
