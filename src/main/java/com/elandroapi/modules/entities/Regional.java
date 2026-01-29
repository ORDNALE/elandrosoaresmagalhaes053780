package com.elandroapi.modules.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "regional")
@Getter
@Setter
public class Regional {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "regional_id_seq")
    @SequenceGenerator(name = "regional_id_seq", sequenceName = "regional_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "id_externo", nullable = false)
    private Integer idExterno;

    @Column(length = 200, nullable = false)
    private String nome;

    @Column(nullable = false)
    private Boolean ativo = true;
}
