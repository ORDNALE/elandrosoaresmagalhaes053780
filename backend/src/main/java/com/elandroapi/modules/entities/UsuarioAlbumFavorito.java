package com.elandroapi.modules.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "usuario_album_favorito")
@IdClass(UsuarioAlbumFavorito.PK.class)
@Getter
@Setter
@NoArgsConstructor
public class UsuarioAlbumFavorito {

    @Id
    @Column(name = "usuario_id")
    private String usuarioId;

    @Id
    @Column(name = "album_id")
    private Long albumId;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {
        private String usuarioId;
        private Long albumId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PK pk = (PK) o;
            return Objects.equals(usuarioId, pk.usuarioId) && Objects.equals(albumId, pk.albumId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(usuarioId, albumId);
        }
    }
}
