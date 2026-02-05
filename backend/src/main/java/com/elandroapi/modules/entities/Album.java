package com.elandroapi.modules.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "album")
@Getter
@Setter
@NoArgsConstructor
public class Album {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "album_id_seq")
    @SequenceGenerator(
            name = "album_id_seq",
            sequenceName = "album_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @ManyToMany
    @JoinTable(
            name = "artista_album",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "artista_id")
    )
    private List<Artista> artistas = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "album_genero",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    private List<Genero> generos = new ArrayList<>();

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CapaAlbum> capas;

    @PrePersist
    public void prePersist() {
        if (this.dataCadastro == null) {
            this.dataCadastro = LocalDateTime.now();
        }
    }
}
