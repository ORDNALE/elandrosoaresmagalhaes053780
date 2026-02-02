package com.elandroapi.modules.dto.request;

import com.elandroapi.modules.enums.TipoArtista;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

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
}
