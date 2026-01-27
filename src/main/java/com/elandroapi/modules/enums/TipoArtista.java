package com.elandroapi.modules.enums;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Tipo de artista", enumeration = {"SOLO", "BANDA"})
public enum TipoArtista {
    SOLO,
    BANDA
}