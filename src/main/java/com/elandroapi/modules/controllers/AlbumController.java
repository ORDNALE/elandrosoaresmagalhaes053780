package com.elandroapi.modules.controllers;

import com.elandroapi.core.pagination.PageRequest;
import com.elandroapi.core.pagination.Paged;
import com.elandroapi.modules.dto.request.AlbumFilterRequest;
import com.elandroapi.modules.dto.request.AlbumRequest;
import com.elandroapi.modules.dto.response.AlbumResponse;
import com.elandroapi.modules.services.AlbumService;
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

@Path("/v1/albuns")
@Tag(name = "Álbum", description = "Gerenciamento de álbuns musicais")
@SecurityRequirement(name = "jwt")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AlbumController {

    @Inject
    AlbumService service;

    @GET
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(
            summary = "Listar álbuns",
            description = "Retorna uma lista paginada de álbuns com filtros opcionais por nome do artista, tipo (SOLO/BANDA) e ordenação alfabética."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Lista de álbuns retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Paged.class))
            ),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado"),
            @APIResponse(responseCode = "403", description = "Acesso negado")
    })
    public Response listar(
            @BeanParam PageRequest pageRequest,
            @BeanParam AlbumFilterRequest filter) {
        return Response.ok(service.listar(pageRequest, filter)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(
            summary = "Buscar álbum por ID",
            description = "Retorna os detalhes de um álbum específico, incluindo artistas e capas."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Álbum encontrado",
                    content = @Content(schema = @Schema(implementation = AlbumResponse.class))
            ),
            @APIResponse(responseCode = "404", description = "Álbum não encontrado"),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado")
    })
    public Response buscar(
            @Parameter(description = "ID do álbum", required = true)
            @PathParam("id") Long id) {
        return Response.ok(service.buscar(id)).build();
    }

    @POST
    @RolesAllowed({"ADMIN"})
    @Operation(
            summary = "Criar novo álbum",
            description = "Cria um novo álbum com os dados fornecidos. Requer permissão de ADMIN."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Álbum criado com sucesso",
                    content = @Content(schema = @Schema(implementation = AlbumResponse.class))
            ),
            @APIResponse(responseCode = "400", description = "Dados inválidos"),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado"),
            @APIResponse(responseCode = "403", description = "Acesso negado - requer permissão ADMIN")
    })
    public Response salvar(@Valid AlbumRequest request) {
        return Response.status(Response.Status.CREATED)
                .entity(service.salvar(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    @Operation(
            summary = "Atualizar álbum",
            description = "Atualiza os dados de um álbum existente. Requer permissão de ADMIN."
    )
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Álbum atualizado com sucesso"),
            @APIResponse(responseCode = "400", description = "Dados inválidos"),
            @APIResponse(responseCode = "404", description = "Álbum não encontrado"),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado"),
            @APIResponse(responseCode = "403", description = "Acesso negado - requer permissão ADMIN")
    })
    public Response atualizar(
            @Parameter(description = "ID do álbum", required = true)
            @PathParam("id") Long id,
            @Valid AlbumRequest request) {
        service.atualizar(id, request);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    @Operation(
            summary = "Excluir álbum",
            description = "Remove um álbum do sistema. Requer permissão de ADMIN."
    )
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Álbum excluído com sucesso"),
            @APIResponse(responseCode = "404", description = "Álbum não encontrado"),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado"),
            @APIResponse(responseCode = "403", description = "Acesso negado - requer permissão ADMIN")
    })
    public Response excluir(
            @Parameter(description = "ID do álbum", required = true)
            @PathParam("id") Long id) {
        service.excluir(id);
        return Response.noContent().build();
    }
}
