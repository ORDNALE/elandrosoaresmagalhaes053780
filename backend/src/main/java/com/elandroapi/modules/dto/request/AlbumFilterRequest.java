package com.elandroapi.modules.dto.request;

import com.elandroapi.modules.enums.TipoArtista;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import java.util.List;

@Getter
@Setter
public class AlbumFilterRequest {

    @QueryParam("nomeArtista")
    @Parameter(description = "Filtrar por nome do artista (busca parcial, case insensitive)")
    private String nomeArtista;

    @QueryParam("titulo")
    @Parameter(description = "Filtrar por título do álbum (busca parcial, case insensitive)")
    private String titulo;

    @QueryParam("tipo")
    @Parameter(description = "Filtrar por tipo de artista (SOLO, BANDA). Pode enviar múltiplos valores.")
    private List<TipoArtista> tipos;

    @QueryParam("sort")
    @DefaultValue("asc")
    @Parameter(description = "Ordenação alfabética pelo nome do artista (asc ou desc)")
    private String sort;

    public boolean hasNomeArtista() {
        return nomeArtista != null && !nomeArtista.isBlank();
    }

    public boolean hasTitulo() {
        return titulo != null && !titulo.isBlank();
    }

    public boolean hasTipos() {
        return tipos != null && !tipos.isEmpty();
    }
}
