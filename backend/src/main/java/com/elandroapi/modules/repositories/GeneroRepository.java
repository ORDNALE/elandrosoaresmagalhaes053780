package com.elandroapi.modules.repositories;

import com.elandroapi.modules.entities.Genero;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GeneroRepository implements PanacheRepository<Genero> {

    public List<Genero> listarAtivos() {
        return list("ativo", true);
    }
}
