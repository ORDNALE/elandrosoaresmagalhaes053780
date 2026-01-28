package com.elandroapi.modules.services;

import com.elandroapi.modules.dto.request.AlbumRequest;
import com.elandroapi.modules.dto.response.AlbumResponse;
import com.elandroapi.modules.entities.Album;
import com.elandroapi.modules.entities.Artista;
import com.elandroapi.modules.enums.TipoArtista;
import com.elandroapi.modules.mappers.AlbumMapper;
import com.elandroapi.modules.repositories.AlbumRepository;
import com.elandroapi.modules.repositories.ArtistaRepository;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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

    private Artista artista;
    private AlbumRequest albumRequest;
    private Album album;
    private AlbumResponse albumResponse;

    @BeforeEach
    void setUp() {
        artista = new Artista();
        artista.setId(1L);
        artista.setNome("U2");
        artista.setTipo(TipoArtista.BANDA);

        albumRequest = new AlbumRequest();
        albumRequest.setTitulo("The Joshua Tree");

        album = new Album();
        album.setId(10L);
        album.setTitulo("The Joshua Tree");
        album.setArtista(artista);

        albumResponse = new AlbumResponse();
        albumResponse.setId(10L);
        albumResponse.setTitulo("The Joshua Tree");
    }

    @Test
    void deveSalvarAlbumComSucesso() {
        when(artistaRepository.findByIdOptional(1L)).thenReturn(Optional.of(artista));
        when(mapper.toModel(any(AlbumRequest.class))).thenReturn(album);
        when(mapper.toResponse(any(Album.class))).thenReturn(albumResponse);

        AlbumResponse response = service.salvar(1L, albumRequest);

        assertNotNull(response);
        assertEquals(albumResponse.getId(), response.getId());

        // Captura o argumento passado para o método persist() para verificação detalhada
        ArgumentCaptor<Album> albumCaptor = ArgumentCaptor.forClass(Album.class);
        verify(albumRepository).persist(albumCaptor.capture());
        Album persistedAlbum = albumCaptor.getValue();

        assertNotNull(persistedAlbum.getArtista());
        assertEquals(1L, persistedAlbum.getArtista().getId());
    }

    @Test
    void deveLancarExcecaoAoSalvarAlbumParaArtistaInexistente() {
        when(artistaRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            service.salvar(99L, albumRequest);
        });
        assertEquals("Artista 99 não encontrado", exception.getMessage());
    }
}
