package com.elandroapi.modules.dto.response;


import com.elandroapi.modules.enums.TipoArtista;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArtistaResponse {

    private Long id;
    private String nome;
    private TipoArtista tipo;
    private List<AlbumResumoResponse> albuns;
    private boolean favorito;
}
