package com.app.modules.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToMany(mappedBy = "albuns")
    private List<Artista> artistas;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CapaAlbum> capas;


}