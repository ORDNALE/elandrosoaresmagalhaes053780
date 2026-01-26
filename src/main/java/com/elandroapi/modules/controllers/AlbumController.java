package com.elandroapi.modules.controllers;

import com.elandroapi.modules.dto.filter.AlbumFilter;
import com.elandroapi.modules.dto.request.AlbumRequest;
import com.elandroapi.modules.dto.response.CapaAlbumResponse;
import com.elandroapi.modules.services.AlbumService;
import com.elandroapi.modules.services.CapaAlbumService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;

@Path("/v1/albuns")
@Tag(name = "Album", description = "Gerenciamento de álbuns")
@SecurityRequirement(name = "jwt")
public class AlbumController {

    @Inject
    AlbumService service;

    @Inject
    CapaAlbumService capaAlbumService;

    @GET
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Listar álbuns", description = "Retorna uma lista paginada de álbuns com opções de filtro.")
    public Response listar(@BeanParam AlbumFilter filter) {
        return Response.ok(service.listar(filter)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Buscar álbum por ID", description = "Retorna os detalhes de um álbum específico.")
    public Response buscarPorId(@PathParam("id") Long id) {
        return Response.ok(service.buscarPorId(id)).build();
    }

    @POST
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Criar novo álbum", description = "Cria um novo álbum com os dados fornecidos.")
    public Response salvar(@Valid AlbumRequest request) {
        return Response.status(Response.Status.CREATED).entity(service.salvar(request)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Atualizar álbum", description = "Atualiza os dados de um álbum existente.")
    public Response atualizar(@PathParam("id") Long id, @Valid AlbumRequest request) {
        return Response.ok(service.atualizar(id, request)).build();
    }

    @POST
    @Path("/{id}/capas")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Upload de capa de álbum", description = "Realiza o upload de uma ou mais imagens para a capa de um álbum.")
    public Response upload(
            @Parameter(description = "ID do álbum", required = true) @PathParam("id") Long id,
            @RestForm("images") List<FileUpload> files) {
        if (files == null || files.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nenhuma imagem enviada")
                    .build();
        }

        List<CapaAlbumResponse> result = capaAlbumService.uploadCapas(id, files);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }
}
