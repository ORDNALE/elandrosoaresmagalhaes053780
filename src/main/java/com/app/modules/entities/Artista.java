package com.app.modules.entities;

import com.app.modules.enums.TipoArtista;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "artista")
@Getter
@Setter
@NoArgsConstructor
public class Artista {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "artista_id_seq")
    @SequenceGenerator(name = "artista_id_seq", sequenceName = "artista_id_seq")
    private Long id;

    @Column(name = "nome")
    private String nome;

    @ManyToMany
    @JoinTable(name = "artista_album", joinColumns = @JoinColumn(name = "artista_id"), inverseJoinColumns = @JoinColumn(name = "album_id"))
    private List<Album> albuns;

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private TipoArtista tipo;


}