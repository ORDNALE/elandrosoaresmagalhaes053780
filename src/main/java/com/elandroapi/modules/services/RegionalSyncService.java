package com.elandroapi.modules.services;

import com.elandroapi.modules.proxy.RegionalExternaClient;
import com.elandroapi.modules.dto.response.RegionalExternaResponse;
import com.elandroapi.modules.dto.response.RegionalSyncResumoResponse;
import com.elandroapi.modules.entities.Regional;
import com.elandroapi.modules.repositories.RegionalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class RegionalSyncService {

    private static final Logger LOG = Logger.getLogger(RegionalSyncService.class);

    @Inject
    RegionalRepository repository;

    @Inject
    @RestClient
    RegionalExternaClient client;

    @Transactional
    public RegionalSyncResumoResponse sincronizar() {
        int inseridos = 0;
        int inativados = 0;
        int alterados = 0;

        List<RegionalExternaResponse> externas = client.listar();
        List<Regional> internasAtivas = repository.listarAtivas();

        Map<Integer, RegionalExternaResponse> externasMap = externas.stream()
                .collect(Collectors.toMap(RegionalExternaResponse::id, Function.identity()));

        Map<Integer, Regional> internasMap = internasAtivas.stream()
                .collect(Collectors.toMap(Regional::getIdExterno, Function.identity()));

        for (RegionalExternaResponse externa : externas) {
            Regional interna = internasMap.get(externa.id());

            if (interna == null) {
                inserirNova(externa);
                inseridos++;
            } else if (!interna.getNome().equals(externa.nome())) {
                inativar(interna);
                inserirNova(externa);
                alterados++;
            }
        }

        for (Regional interna : internasAtivas) {
            if (!externasMap.containsKey(interna.getIdExterno())) {
                inativar(interna);
                inativados++;
            }
        }

        LOG.infof("Sincronização finalizada | inseridos=%d alterados=%d inativados=%d",
                inseridos, alterados, inativados);

        return new RegionalSyncResumoResponse(inseridos, inativados, alterados);
    }

    private void inserirNova(RegionalExternaResponse dto) {
        LOG.infof("Inserindo regional idExterno=%d nome=%s", dto.id(), dto.nome());
        Regional entity = new Regional();
        entity.setIdExterno(dto.id());
        entity.setNome(dto.nome());
        entity.setAtivo(true);
        repository.persist(entity);
    }

    private void inativar(Regional entity) {
        LOG.infof("Inativando regional id=%d nome=%s", entity.getId(), entity.getNome());
        entity.setAtivo(false);
    }
}
