package com.elandroapi.modules.repositories;

import com.elandroapi.modules.entities.Artista;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ArtistaRepository implements PanacheRepository<Artista> {
}