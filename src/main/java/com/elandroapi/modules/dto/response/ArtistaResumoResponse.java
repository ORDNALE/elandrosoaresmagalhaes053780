package com.elandroapi.modules.dto.response;

import com.elandroapi.modules.enums.TipoArtista;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtistaResumoResponse {

    private Long id;
    private String nome;
    private TipoArtista tipo;
}
