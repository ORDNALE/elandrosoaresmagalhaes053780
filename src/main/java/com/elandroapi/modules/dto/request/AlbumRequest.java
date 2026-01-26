package com.elandroapi.modules.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AlbumRequest {

    @NotBlank(message = "O título do álbum não pode ser vazio.")
    private String titulo;

    @NotEmpty(message = "O álbum deve estar associado a pelo menos um artista.")
    private List<Long> artistaIds;
}
