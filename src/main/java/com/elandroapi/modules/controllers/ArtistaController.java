package com.elandroapi.modules.controllers;

import com.elandroapi.core.pagination.PageRequest;
import com.elandroapi.core.pagination.Paged;
import com.elandroapi.modules.dto.request.AlbumRequest;
import com.elandroapi.modules.dto.request.ArtistaFilterRequest;
import com.elandroapi.modules.dto.request.ArtistaRequest;
import com.elandroapi.modules.dto.response.AlbumResponse;
import com.elandroapi.modules.dto.response.ArtistaResponse;
import com.elandroapi.modules.services.AlbumService;
import com.elandroapi.modules.services.ArtistaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/v1/artistas")
@Tag(name = "2. Artista", description = "Gerenciamento de artistas (cantores solo e bandas)")
@SecurityRequirement(name = "jwt")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArtistaController {

    @Inject
    ArtistaService service;

    @Inject
    AlbumService albumService;

    @GET
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(
            summary = "Listar artistas",
            description = "Retorna uma lista paginada de artistas com filtros opcionais por nome, tipo (SOLO/BANDA) e ordenação alfabética."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Lista de artistas retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Paged.class))
            ),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado"),
            @APIResponse(responseCode = "403", description = "Acesso negado")
    })
    public Response listar(
            @BeanParam PageRequest pageRequest,
            @BeanParam ArtistaFilterRequest filter) {
        return Response.ok(service.listar(pageRequest, filter)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(
            summary = "Buscar artista por ID",
            description = "Retorna os detalhes de um artista específico."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Artista encontrado",
                    content = @Content(schema = @Schema(implementation = ArtistaResponse.class))
            ),
            @APIResponse(responseCode = "404", description = "Artista não encontrado"),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado")
    })
    public Response buscar(
            @Parameter(description = "ID do artista", required = true)
            @PathParam("id") Long id) {
        return Response.ok(service.buscar(id)).build();
    }

    @POST
    @RolesAllowed({"ADMIN"})
    @Operation(
            summary = "Criar novo artista",
            description = "Cria um novo artista com os dados fornecidos. Requer permissão de ADMIN."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Artista criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ArtistaResponse.class))
            ),
            @APIResponse(responseCode = "400", description = "Dados inválidos"),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado"),
            @APIResponse(responseCode = "403", description = "Acesso negado - requer permissão ADMIN")
    })
    public Response salvar(@Valid ArtistaRequest request) {
        return Response.status(Response.Status.CREATED)
                .entity(service.salvar(request))
                .build();
    }

    @POST
    @Path("/{artistaId}/albuns")
    @RolesAllowed({"ADMIN"})
    @Operation(
            summary = "Criar novo álbum para um artista",
            description = "Cria um novo álbum para um artista específico. Requer permissão de ADMIN."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Álbum criado com sucesso",
                    content = @Content(schema = @Schema(implementation = AlbumResponse.class))
            ),
            @APIResponse(responseCode = "400", description = "Dados inválidos"),
            @APIResponse(responseCode = "404", description = "Artista não encontrado"),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado"),
            @APIResponse(responseCode = "403", description = "Acesso negado - requer permissão ADMIN")
    })
    public Response salvarAlbum(
            @Parameter(description = "ID do artista", required = true)
            @PathParam("artistaId") Long artistaId,
            @Valid AlbumRequest request) {
        return Response.status(Response.Status.CREATED)
                .entity(albumService.salvar(artistaId, request))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    @Operation(
            summary = "Atualizar artista",
            description = "Atualiza os dados de um artista existente. Requer permissão de ADMIN."
    )
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Artista atualizado com sucesso"),
            @APIResponse(responseCode = "400", description = "Dados inválidos"),
            @APIResponse(responseCode = "404", description = "Artista não encontrado"),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado"),
            @APIResponse(responseCode = "403", description = "Acesso negado - requer permissão ADMIN")
    })
    public Response atualizar(
            @Parameter(description = "ID do artista", required = true)
            @PathParam("id") Long id,
            @Valid ArtistaRequest request) {
        service.atualizar(id, request);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    @Operation(
            summary = "Excluir artista",
            description = "Remove um artista do sistema. Requer permissão de ADMIN."
    )
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Artista excluído com sucesso"),
            @APIResponse(responseCode = "404", description = "Artista não encontrado"),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado"),
            @APIResponse(responseCode = "403", description = "Acesso negado - requer permissão ADMIN")
    })
    public Response excluir(
            @Parameter(description = "ID do artista", required = true)
            @PathParam("id") Long id) {
        service.excluir(id);
        return Response.noContent().build();
    }
}
