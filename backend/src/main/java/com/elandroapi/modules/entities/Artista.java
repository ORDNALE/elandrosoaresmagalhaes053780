package com.elandroapi.modules.entities;

import com.elandroapi.modules.enums.TipoArtista;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
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
    @SequenceGenerator(name = "artista_id_seq", sequenceName = "artista_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "nome")
    private String nome;

    @ManyToMany(mappedBy = "artistas")
    private List<Album> albuns = new ArrayList<>();

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private TipoArtista tipo;
}
