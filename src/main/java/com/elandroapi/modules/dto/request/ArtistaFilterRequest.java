package com.elandroapi.modules.dto.request;

import com.elandroapi.modules.enums.TipoArtista;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

@Getter
@Setter
public class ArtistaFilterRequest {

    @QueryParam("nome")
    @Parameter(description = "Filtrar por nome do artista (busca parcial, case insensitive)")
    private String nome;

    @QueryParam("tipo")
    @Parameter(description = "Filtrar por tipo de artista (SOLO ou BANDA)")
    private TipoArtista tipo;

    @QueryParam("sort")
    @DefaultValue("asc")
    @Parameter(description = "Ordenação alfabética pelo nome (asc ou desc)")
    private String sort;

    public boolean hasNome() {
        return nome != null && !nome.isBlank();
    }

    public boolean hasTipo() {
        return tipo != null;
    }
}
