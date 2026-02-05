package com.elandroapi.modules.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Getter
@Setter
public class AlbumRequest {

    @NotBlank(message = "titulo deve ser informado")
    @Schema(examples = "ao vivo em narnia")
    private String titulo;

    @NotEmpty(message = "artistaIds deve conter ao menos um artista")
    private List<Long> artistaIds;

    @Schema(description = "IDs dos gêneros musicais")
    private List<Long> generoIds;
}
