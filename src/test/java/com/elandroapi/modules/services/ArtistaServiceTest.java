package com.elandroapi.modules.services;

import com.elandroapi.modules.dto.request.ArtistaRequest;
import com.elandroapi.modules.dto.response.ArtistaResponse;
import com.elandroapi.modules.entities.Artista;
import com.elandroapi.modules.enums.TipoArtista;
import com.elandroapi.modules.mappers.ArtistaMapper;
import com.elandroapi.modules.repositories.ArtistaRepository;
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
class ArtistaServiceTest {

    @InjectMocks
    ArtistaService service;

    @Mock
    ArtistaRepository repository;

    @Mock
    ArtistaMapper mapper;

    private ArtistaRequest artistaRequest;
    private Artista artista;
    private ArtistaResponse artistaResponse;

    @BeforeEach
    void setUp() {
        // Dados de entrada
        artistaRequest = new ArtistaRequest();
        artistaRequest.setNome("U2");
        artistaRequest.setTipo(TipoArtista.BANDA);

        // Entidade Mapeada
        artista = new Artista();
        artista.setId(1L);
        artista.setNome("U2");
        artista.setTipo(TipoArtista.BANDA);

        // Resposta final
        artistaResponse = new ArtistaResponse();
        artistaResponse.setId(1L);
        artistaResponse.setNome("U2");
        artistaResponse.setTipo(TipoArtista.BANDA);
    }

    @Test
    void deveSalvarArtistaComSucesso() {
        when(mapper.toModel(any(ArtistaRequest.class))).thenReturn(artista);
        when(mapper.toResponse(any(Artista.class))).thenReturn(artistaResponse);

        ArtistaResponse response = service.salvar(artistaRequest);

        assertNotNull(response);
        assertEquals(artistaResponse.getId(), response.getId());
        assertEquals(artistaResponse.getNome(), response.getNome());
        assertEquals(artistaResponse.getTipo(), response.getTipo());
        verify(repository, times(1)).persist(artista);
    }

    @Test
    void deveBuscarArtistaPorIdComSucesso() {
        when(repository.findByIdOptional(1L)).thenReturn(Optional.of(artista));
        when(mapper.toResponse(artista)).thenReturn(artistaResponse);

        ArtistaResponse response = service.buscar(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void deveLancarExcecaoAoBuscarArtistaInexistente() {
        when(repository.findByIdOptional(99L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            service.buscar(99L);
        });
        assertEquals("Artista 99 não encontrado", exception.getMessage());
    }

    @Test
    void deveExcluirArtistaComSucesso() {
        service.excluir(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}
