package com.elandroapi.websocket;

import java.time.LocalDateTime;

public record AlbumNotificationEvent(
        Long albumId,
        String titulo,
        LocalDateTime timestamp,
        String message
) {
    public static AlbumNotificationEvent novoAlbum(Long albumId, String titulo) {
        return new AlbumNotificationEvent(albumId, titulo, LocalDateTime.now(), "Novo álbum cadastrado");
    }
}
