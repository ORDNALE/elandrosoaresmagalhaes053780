package com.elandroapi.modules.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
public class AlbumRequest {

    @NotBlank(message = "titulo deve ser informado")
    @Schema(examples = "ao vivo em narnia")
    private String titulo;

}