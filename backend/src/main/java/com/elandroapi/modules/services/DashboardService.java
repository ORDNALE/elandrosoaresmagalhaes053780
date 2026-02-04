package com.elandroapi.modules.services;

import com.elandroapi.modules.dto.response.DashboardResponse;
import com.elandroapi.modules.repositories.AlbumRepository;
import com.elandroapi.modules.repositories.ArtistaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@ApplicationScoped
public class DashboardService {

    @Inject
    ArtistaRepository artistaRepository;

    @Inject
    AlbumRepository albumRepository;

    public DashboardResponse obterTotais() {
        long totalArtistas = artistaRepository.count();
        long totalAlbuns = albumRepository.count();

        LocalDateTime inicioSemana = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();

        long novidadesSemana = albumRepository.contarNovidadesSemana(inicioSemana);

        return new DashboardResponse(totalArtistas, totalAlbuns, novidadesSemana);
    }
}
