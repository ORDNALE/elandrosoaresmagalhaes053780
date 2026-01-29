package com.elandroapi.modules.dto.response;

public record RegionalResponse(
        Long id,
        Integer idExterno,
        String nome,
        Boolean ativo
) {
}
