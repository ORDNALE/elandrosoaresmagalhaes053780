package com.elandroapi.modules.repositories;

import com.elandroapi.modules.dto.filter.ArtistaFilter;
import com.elandroapi.modules.entities.Artista;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class ArtistaRepository implements PanacheRepository<Artista> {

    public PanacheQuery<Artista> find(ArtistaFilter filter) {
        StringBuilder query = new StringBuilder("FROM Artista a WHERE 1=1 ");
        Map<String, Object> params = new HashMap<>();

        if (filter.getNome() != null && !filter.getNome().isBlank()) {
            query.append("AND lower(a.nome) LIKE lower(:nome) ");
            params.put("nome", "%" + filter.getNome() + "%");
        }

        if (filter.getTipo() != null) {
            query.append("AND a.tipo = :tipo ");
            params.put("tipo", filter.getTipo());
        }

        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSort()) ? Sort.Direction.Descending : Sort.Direction.Ascending;
        Sort sort = Sort.by("nome").direction(direction);

        return find(query.toString(), sort, params);
    }
}
