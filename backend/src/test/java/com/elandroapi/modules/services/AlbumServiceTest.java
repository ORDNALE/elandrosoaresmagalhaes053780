package com.elandroapi.modules.services;

import com.elandroapi.modules.dto.request.AlbumRequest;
import com.elandroapi.modules.dto.response.AlbumResponse;
import com.elandroapi.modules.entities.Album;
import com.elandroapi.modules.entities.Artista;
import com.elandroapi.modules.enums.TipoArtista;
import com.elandroapi.modules.mappers.AlbumMapper;
import com.elandroapi.modules.repositories.AlbumRepository;
import com.elandroapi.modules.repositories.ArtistaRepository;
import com.elandroapi.websocket.AlbumNotificationEvent;
import com.elandroapi.websocket.AlbumWebSocket;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @InjectMocks
    AlbumService service;

    @Mock
    AlbumRepository albumRepository;

    @Mock
    ArtistaRepository artistaRepository;

    @Mock
    AlbumMapper mapper;

    @Mock
    AlbumWebSocket.Broadcaster broadcaster;

    private Artista artista1;
    private Artista artista2;
    private AlbumRequest albumRequest;
    private Album album;
    private AlbumResponse albumResponse;

    @BeforeEach
    void setUp() {
        artista1 = new Artista();
        artista1.setId(1L);
        artista1.setNome("Queen");
        artista1.setTipo(TipoArtista.BANDA);

        artista2 = new Artista();
        artista2.setId(2L);
        artista2.setNome("David Bowie");
        artista2.setTipo(TipoArtista.SOLO);

        albumRequest = new AlbumRequest();
        albumRequest.setTitulo("Under Pressure");
        albumRequest.setArtistaIds(List.of(1L, 2L));

        album = new Album();
        album.setId(10L);
        album.setTitulo("Under Pressure");
        album.setArtistas(new ArrayList<>());

        albumResponse = new AlbumResponse();
        albumResponse.setId(10L);
        albumResponse.setTitulo("Under Pressure");
    }

    @Test
    void deveSalvarAlbumComMultiplosArtistas() {
        when(artistaRepository.list("id IN ?1", List.of(1L, 2L))).thenReturn(List.of(artista1, artista2));
        when(mapper.toModel(any(AlbumRequest.class))).thenReturn(album);
        when(mapper.toResponse(any(Album.class))).thenReturn(albumResponse);

        AlbumResponse response = service.salvar(albumRequest);

        assertNotNull(response);
        assertEquals(albumResponse.getId(), response.getId());

        ArgumentCaptor<Album> albumCaptor = ArgumentCaptor.forClass(Album.class);
        verify(albumRepository).persist(albumCaptor.capture());
        Album persistedAlbum = albumCaptor.getValue();

        assertNotNull(persistedAlbum.getArtistas());
        assertEquals(2, persistedAlbum.getArtistas().size());

        verify(broadcaster).broadcast(any(AlbumNotificationEvent.class));
    }

    @Test
    void deveSalvarAlbumComUmArtista() {
        albumRequest.setArtistaIds(List.of(1L));

        when(artistaRepository.list("id IN ?1", List.of(1L))).thenReturn(List.of(artista1));
        when(mapper.toModel(any(AlbumRequest.class))).thenReturn(album);
        when(mapper.toResponse(any(Album.class))).thenReturn(albumResponse);

        AlbumResponse response = service.salvar(albumRequest);

        assertNotNull(response);

        ArgumentCaptor<Album> albumCaptor = ArgumentCaptor.forClass(Album.class);
        verify(albumRepository).persist(albumCaptor.capture());
        Album persistedAlbum = albumCaptor.getValue();

        assertEquals(1, persistedAlbum.getArtistas().size());
        assertEquals(1L, persistedAlbum.getArtistas().get(0).getId());
    }

    @Test
    void deveLancarExcecaoAoSalvarAlbumComArtistaInexistente() {
        albumRequest.setArtistaIds(List.of(1L, 99L));

        when(artistaRepository.list("id IN ?1", List.of(1L, 99L))).thenReturn(List.of(artista1));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            service.salvar(albumRequest);
        });

        assertTrue(exception.getMessage().contains("99"));
    }
}
