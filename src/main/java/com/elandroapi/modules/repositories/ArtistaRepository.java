package com.app.modules.repositories;

import com.app.modules.entities.Artista;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ArtistaRepository implements PanacheRepository<Artista> {
}