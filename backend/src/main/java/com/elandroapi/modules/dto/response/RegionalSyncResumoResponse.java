package com.elandroapi.modules.dto.response;

public record RegionalSyncResumoResponse(
        int inseridos,
        int inativados,
        int alterados
) {
}
