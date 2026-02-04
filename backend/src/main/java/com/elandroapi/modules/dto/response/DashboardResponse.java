package com.elandroapi.modules.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DashboardResponse {
    private long totalArtistas;
    private long totalAlbuns;
    private long novidadesSemana;
}
