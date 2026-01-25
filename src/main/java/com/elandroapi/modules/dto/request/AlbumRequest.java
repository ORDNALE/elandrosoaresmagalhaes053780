package com.elandroapi.modules.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlbumRequest {

    @NotBlank(message = "titulo deve ser informado")
    private String titulo;

}