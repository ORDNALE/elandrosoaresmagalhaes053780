package com.app.modules.repositories;

import com.app.modules.entities.CapaAlbum;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CapaAlbumRepository implements PanacheRepository<CapaAlbum> {
}