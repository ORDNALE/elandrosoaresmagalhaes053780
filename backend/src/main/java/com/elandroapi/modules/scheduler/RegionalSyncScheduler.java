package com.elandroapi.modules.scheduler;

import com.elandroapi.modules.services.RegionalSyncService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class RegionalSyncScheduler {

    private static final Logger LOG = Logger.getLogger(RegionalSyncScheduler.class);

    @Inject
    RegionalSyncService service;

    @Scheduled(cron = "0 0 6 * * ?")
    void syncDiaria() {
        LOG.info("Iniciando sincronização automática de regionais");
        service.sincronizar();
    }
}
