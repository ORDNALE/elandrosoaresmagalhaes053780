package com.elandroapi.modules.dto.filter;

import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

public class RegionalFilterRequest {

    @QueryParam("nome")
    @Parameter(description = "Filtrar por nome (busca parcial)")
    private String nome;

    @QueryParam("ativo")
    @Parameter(description = "Filtrar por status ativo (true/false)")
    private Boolean ativo;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public boolean hasNome() {
        return nome != null && !nome.isBlank();
    }

    public boolean hasAtivo() {
        return ativo != null;
    }
}
