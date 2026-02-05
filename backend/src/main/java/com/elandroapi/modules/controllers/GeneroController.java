package com.elandroapi.modules.controllers;

import com.elandroapi.modules.dto.response.GeneroResponse;
import com.elandroapi.modules.mappers.GeneroMapper;
import com.elandroapi.modules.repositories.GeneroRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/v1/generos")
@Tag(name = "Gênero", description = "Gêneros musicais")
@SecurityRequirement(name = "jwt")
@Produces(MediaType.APPLICATION_JSON)
public class GeneroController {

    @Inject
    GeneroRepository repository;

    @Inject
    GeneroMapper mapper;

    @GET
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Listar gêneros ativos")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Lista de gêneros retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = GeneroResponse.class))
            ),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado")
    })
    public Response listar() {
        List<GeneroResponse> response = repository.listarAtivos().stream()
                .map(mapper::toResponse)
                .toList();
        return Response.ok(response).build();
    }
}
