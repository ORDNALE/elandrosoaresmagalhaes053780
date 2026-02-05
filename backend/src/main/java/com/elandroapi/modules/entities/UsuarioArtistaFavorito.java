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
@Table(name = "usuario_artista_favorito")
@IdClass(UsuarioArtistaFavorito.PK.class)
@Getter
@Setter
@NoArgsConstructor
public class UsuarioArtistaFavorito {

    @Id
    @Column(name = "usuario_id")
    private String usuarioId;

    @Id
    @Column(name = "artista_id")
    private Long artistaId;

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
        private Long artistaId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PK pk = (PK) o;
            return Objects.equals(usuarioId, pk.usuarioId) && Objects.equals(artistaId, pk.artistaId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(usuarioId, artistaId);
        }
    }
}
