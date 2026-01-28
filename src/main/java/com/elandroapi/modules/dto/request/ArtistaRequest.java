package com.elandroapi.modules.dto.request;

import com.elandroapi.modules.enums.TipoArtista;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ArtistaRequest {

    @NotBlank(message = "nome deve ser informado")
    @Schema(examples = "Tonnin do violino")
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