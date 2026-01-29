package com.elandroapi.modules.services;

import com.elandroapi.core.pagination.PageRequest;
import com.elandroapi.core.pagination.Paged;
import com.elandroapi.modules.dto.filter.RegionalFilterRequest;
import com.elandroapi.modules.dto.response.RegionalResponse;
import com.elandroapi.modules.repositories.RegionalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RegionalService {

    @Inject
    RegionalRepository repository;

    public Paged<RegionalResponse> listar(PageRequest pageRequest, RegionalFilterRequest filter) {
        var query = repository.findByFilters(filter);
        query.page(pageRequest.toPage());

        var content = query.list().stream()
                .map(r -> new RegionalResponse(r.getId(), r.getIdExterno(), r.getNome(), r.getAtivo()))
                .toList();

        return new Paged<>(query, content);
    }
}
