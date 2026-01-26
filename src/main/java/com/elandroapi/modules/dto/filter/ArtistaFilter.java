package com.elandroapi.modules.dto.filter;

import com.elandroapi.modules.enums.TipoArtista;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtistaFilter implements PageableFilter {

    @QueryParam("nome")
    private String nome;

    @QueryParam("tipo")
    private TipoArtista tipo;

    @QueryParam("sort")
    private String sort = "asc";

    @QueryParam("page")
    private int page = 0;

    @QueryParam("size")
    private int size = 10;
}
