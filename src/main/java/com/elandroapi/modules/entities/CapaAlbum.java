package com.elandroapi.modules.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "capa_album")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CapaAlbum {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "capa_album_id_seq")
    @SequenceGenerator(name = "capa_album_id_seq", sequenceName = "capa_album_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "bucket")
    private String bucket;

    @Column(name = "hash")
    private String hash;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "tamanho")
    private Long tamanho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;


}