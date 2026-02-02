package com.elandroapi.modules.repositories;

import com.elandroapi.modules.dto.filter.RegionalFilterRequest;
import com.elandroapi.modules.entities.Regional;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class RegionalRepository implements PanacheRepository<Regional> {

    public List<Regional> listarAtivas() {
        return list("ativo", true);
    }

    public PanacheQuery<Regional> findByFilters(RegionalFilterRequest filter) {
        List<String> conditions = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (filter.hasNome()) {
            conditions.add("LOWER(nome) LIKE LOWER(:nome)");
            params.put("nome", "%" + filter.getNome() + "%");
        }

        if (filter.hasAtivo()) {
            conditions.add("ativo = :ativo");
            params.put("ativo", filter.getAtivo());
        }

        Sort sortOrder = Sort.by("nome").ascending();

        if (conditions.isEmpty()) {
            return findAll(sortOrder);
        }

        String query = String.join(" AND ", conditions);
        return find(query, sortOrder, params);
    }
}
