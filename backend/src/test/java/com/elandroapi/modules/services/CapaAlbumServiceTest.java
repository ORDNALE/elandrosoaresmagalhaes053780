package com.elandroapi.modules.services;

import com.elandroapi.modules.dto.response.CapaAlbumResponse;
import com.elandroapi.modules.entities.Album;
import com.elandroapi.modules.entities.CapaAlbum;
import com.elandroapi.modules.mappers.CapaAlbumMapper;
import com.elandroapi.modules.repositories.AlbumRepository;
import com.elandroapi.modules.repositories.CapaAlbumRepository;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CapaAlbumServiceTest {

    @InjectMocks
    CapaAlbumService service;

    @Mock
    MinioClient minioClient;

    @Mock
    CapaAlbumRepository repository;

    @Mock
    AlbumRepository albumRepository;

    @Mock
    CapaAlbumMapper mapper;

    @Mock
    PanacheQuery<CapaAlbum> panacheQuery;

    private Album album;
    private CapaAlbum capaAlbum;
    private CapaAlbumResponse capaAlbumResponse;

    @BeforeEach
    void setUp() {
        album = new Album();
        album.setId(1L);
        album.setTitulo("The Joshua Tree");

        capaAlbum = new CapaAlbum();
        capaAlbum.setId(100L);
        capaAlbum.setAlbum(album);
        capaAlbum.setBucket("test-bucket");
        capaAlbum.setHash("test-hash");

        capaAlbumResponse = new CapaAlbumResponse();
        capaAlbumResponse.setId(100L);
    }

    @Test
    void deveGerarLinkDeCapaComSucesso() throws Exception {
        String fakeUrl = "http://localhost:19000/test-bucket/test-hash?presigned";
        capaAlbumResponse.setUrl(fakeUrl);
        when(albumRepository.findByIdOptional(1L)).thenReturn(Optional.of(album));
        when(repository.find("id = ?1 and album.id = ?2", 100L, 1L)).thenReturn(panacheQuery);
        when(panacheQuery.firstResultOptional()).thenReturn(Optional.of(capaAlbum));
        when(mapper.toResponse(capaAlbum)).thenReturn(capaAlbumResponse);

        CapaAlbumResponse response = service.gerarLinkCapa(1L, 100L);

        assertNotNull(response);
        assertEquals(fakeUrl, response.getUrl());
        verify(mapper).toResponse(capaAlbum);
    }

    @Test
    void deveLancarExcecaoAoGerarLinkParaCapaInexistente() {
        when(albumRepository.findByIdOptional(1L)).thenReturn(Optional.of(album));
        when(repository.find("id = ?1 and album.id = ?2", 999L, 1L)).thenReturn(panacheQuery);
        when(panacheQuery.firstResultOptional()).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            service.gerarLinkCapa(1L, 999L);
        });
        assertEquals("Capa 999 não encontrada no álbum 1", exception.getMessage());
    }

    @Test
    void deveExcluirCapaComSucesso() throws Exception {
        when(albumRepository.findByIdOptional(1L)).thenReturn(Optional.of(album));
        when(repository.find("id = ?1 and album.id = ?2", 100L, 1L)).thenReturn(panacheQuery);
        when(panacheQuery.firstResultOptional()).thenReturn(Optional.of(capaAlbum));

        service.excluirCapa(1L, 100L);

        verify(minioClient, times(1)).removeObject(any(RemoveObjectArgs.class));
        verify(repository, times(1)).delete(capaAlbum);
    }

    @Test
    void deveLancarExcecaoAoExcluirCapaDeAlbumInexistente() {
        when(albumRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            service.excluirCapa(99L, 100L);
        });
        assertEquals("Álbum 99 não encontrado", exception.getMessage());
    }
}
