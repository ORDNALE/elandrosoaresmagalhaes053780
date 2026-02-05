package com.elandroapi.modules.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AlbumResponse {

    private Long id;
    private String titulo;
    private Integer ano;
    private List<ArtistaResumoResponse> artistas;
    private List<GeneroResponse> generos;
    private List<CapaAlbumResponse> capas;
    private boolean favorito;
}
