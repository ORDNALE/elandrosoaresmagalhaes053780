package com.elandroapi.modules.dto.filter;

import com.elandroapi.modules.enums.TipoArtista;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AlbumFilter implements PageableFilter {

    @QueryParam("titulo")
    private String titulo;

    @QueryParam("tipos")
    private List<TipoArtista> tipos;

    @QueryParam("page")
    private int page = 0;

    @QueryParam("size")
    private int size = 10;
}
