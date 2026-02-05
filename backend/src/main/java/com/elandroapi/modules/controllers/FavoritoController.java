package com.elandroapi.modules.controllers;

import com.elandroapi.modules.services.FavoritoService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/v1/me")
@Tag(name = "Me / Favoritos", description = "Gerenciamento de favoritos do usuário logado")
@SecurityRequirement(name = "jwt")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FavoritoController {

    @Inject
    FavoritoService service;

    @Inject
    JsonWebToken jwt;

    @POST
    @Path("/artistas/{artistaId}/favoritar")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Favoritar artista", description = "Adiciona um artista aos favoritos do usuário logado.")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Artista favoritado com sucesso"),
            @APIResponse(responseCode = "404", description = "Artista não encontrado"),
            @APIResponse(responseCode = "409", description = "Artista já favoritado"),
            @APIResponse(responseCode = "401", description = "Não autorizado (Token inválido ou ausente)")
    })
    public Response favoritarArtista(@PathParam("artistaId") Long artistaId) {
        service.favoritarArtista(jwt.getSubject(), artistaId);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/artistas/{artistaId}/favoritar")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Desfavoritar artista", description = "Remove um artista dos favoritos do usuário logado.")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Artista desfavoritado com sucesso"),
            @APIResponse(responseCode = "404", description = "Favorito não encontrado ou Artista inexistente"),
            @APIResponse(responseCode = "401", description = "Não autorizado")
    })
    public Response desfavoritarArtista(@PathParam("artistaId") Long artistaId) {
        service.desfavoritarArtista(jwt.getSubject(), artistaId);
        return Response.noContent().build();
    }

    @POST
    @Path("/albuns/{albumId}/favoritar")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Favoritar álbum", description = "Adiciona um álbum aos favoritos do usuário logado.")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Álbum favoritado com sucesso"),
            @APIResponse(responseCode = "404", description = "Álbum não encontrado"),
            @APIResponse(responseCode = "409", description = "Álbum já favoritado"),
            @APIResponse(responseCode = "401", description = "Não autorizado")
    })
    public Response favoritarAlbum(@PathParam("albumId") Long albumId) {
        service.favoritarAlbum(jwt.getSubject(), albumId);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/albuns/{albumId}/favoritar")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Desfavoritar álbum", description = "Remove um álbum dos favoritos do usuário logado.")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Álbum desfavoritado com sucesso"),
            @APIResponse(responseCode = "404", description = "Favorito não encontrado ou Álbum inexistente"),
            @APIResponse(responseCode = "401", description = "Não autorizado")
    })
    public Response desfavoritarAlbum(@PathParam("albumId") Long albumId) {
        service.desfavoritarAlbum(jwt.getSubject(), albumId);
        return Response.noContent().build();
    }
}
