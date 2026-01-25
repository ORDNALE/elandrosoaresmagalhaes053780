package com.app.modules.dto.request;

import com.app.modules.enums.TipoArtista;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ArtistaRequest {

    @NotBlank(message = "nome deve ser informado")
    private String nome;

    @NotNull(message = "tipo deve ser informado")
    private TipoArtista tipo;

    @Valid
    private List<AlbumId> albuns;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlbumId {
        @NotNull(message = "O ID do álbum não pode ser nulo")
        private Long id;
    }
}