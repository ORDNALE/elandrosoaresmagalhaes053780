package com.elandroapi.modules.controllers;

import com.elandroapi.core.pagination.PageRequest;
import com.elandroapi.modules.dto.request.AlbumRequest;
import com.elandroapi.modules.enums.TipoArtista;
import com.elandroapi.modules.services.AlbumService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/v1/albuns")
@Tag(name = "Album", description = "Gerenciamento de álbuns")
@SecurityRequirement(name = "jwt")
public class AlbumController {

    @Inject
    AlbumService service;

    @GET
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Listar álbuns", description = "Retorna uma lista paginada de álbuns.")
    public Response listar(@BeanParam PageRequest pageRequest,
                           @QueryParam("tipo") List<TipoArtista> tipos) {
        return Response.ok(service.listar(pageRequest, tipos)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Buscar álbum por ID", description = "Retorna os detalhes de um álbum específico.")
    public Response buscar(@PathParam("id") Long id) {
        return Response.ok(service.buscar(id)).build();
    }

    @POST
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Criar novo álbum", description = "Cria um novo álbum com os dados fornecidos.")
    public Response salvar(@Valid AlbumRequest request) {
        return Response.status(Response.Status.CREATED)
                .entity(service.salvar(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Atualizar álbum", description = "Atualiza os dados de um álbum existente.")
    public Response atualizar(@PathParam("id") Long id, @Valid AlbumRequest request) {
        service.atualizar(id, request);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Excluir álbum", description = "Remove um álbum do sistema.")
    public Response excluir(@PathParam("id") Long id) {
        service.excluir(id);
        return Response.noContent().build();
    }
}