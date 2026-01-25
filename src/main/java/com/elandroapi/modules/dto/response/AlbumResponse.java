package com.app.modules.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AlbumResponse {

    private Long id;
    private String titulo;
    private List<CapaAlbumResponse> capas;

}