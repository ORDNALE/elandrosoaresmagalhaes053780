package com.elandroapi.modules.controllers;

import com.elandroapi.modules.dto.response.CapaAlbumResponse;
import com.elandroapi.modules.services.CapaAlbumService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
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
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;

@Path("/v1/albuns")
@Tag(name = "Capa de Álbum", description = "Gerenciamento de capas de álbuns no MinIO")
@SecurityRequirement(name = "jwt")
@Produces(MediaType.APPLICATION_JSON)
public class CapaAlbumController {

    @Inject
    CapaAlbumService service;

    @POST
    @Path("/{albumId}/capas")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed({"ADMIN"})
    @Operation(
            summary = "Upload de capa de álbum",
            description = "Realiza o upload de uma ou mais imagens para a capa de um álbum. As imagens são armazenadas no MinIO."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Capas enviadas com sucesso",
                    content = @Content(schema = @Schema(implementation = CapaAlbumResponse.class))
            ),
            @APIResponse(responseCode = "400", description = "Nenhuma imagem enviada"),
            @APIResponse(responseCode = "404", description = "Álbum não encontrado"),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado"),
            @APIResponse(responseCode = "403", description = "Acesso negado - requer permissão ADMIN")
    })
    public Response upload(
            @Parameter(description = "ID do álbum", required = true)
            @PathParam("albumId") Long albumId,
            @RestForm("images") List<FileUpload> files) {
        if (files == null || files.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nenhuma imagem enviada")
                    .build();
        }

        List<CapaAlbumResponse> result = service.uploadCapas(albumId, files);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @GET
    @Path("/{albumId}/capas")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(
            summary = "Listar capas de um álbum",
            description = "Retorna todas as capas de um álbum com URLs pré-assinadas do MinIO (válidas por 30 minutos)."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Lista de capas retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = CapaAlbumResponse.class))
            ),
            @APIResponse(responseCode = "404", description = "Álbum não encontrado"),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado")
    })
    public Response listarCapas(
            @Parameter(description = "ID do álbum", required = true)
            @PathParam("albumId") Long albumId) {
        return Response.ok(service.listarCapas(albumId)).build();
    }

    @GET
    @Path("/{albumId}/capas/{capaId}")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(
            summary = "Obter URL de capa específica",
            description = "Retorna a URL pré-assinada do MinIO para uma capa específica (válida por 30 minutos)."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "URL da capa retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = CapaAlbumResponse.class))
            ),
            @APIResponse(responseCode = "404", description = "Álbum ou capa não encontrado"),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado")
    })
    public Response buscarCapa(
            @Parameter(description = "ID do álbum", required = true)
            @PathParam("albumId") Long albumId,
            @Parameter(description = "ID da capa", required = true)
            @PathParam("capaId") Long capaId) {
        return Response.ok(service.buscarCapa(albumId, capaId)).build();
    }

    @DELETE
    @Path("/{albumId}/capas/{capaId}")
    @RolesAllowed({"ADMIN"})
    @Operation(
            summary = "Excluir capa de álbum",
            description = "Remove uma capa específica do álbum e do MinIO. Requer permissão de ADMIN."
    )
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Capa excluída com sucesso"),
            @APIResponse(responseCode = "404", description = "Álbum ou capa não encontrado"),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado"),
            @APIResponse(responseCode = "403", description = "Acesso negado - requer permissão ADMIN")
    })
    public Response excluirCapa(
            @Parameter(description = "ID do álbum", required = true)
            @PathParam("albumId") Long albumId,
            @Parameter(description = "ID da capa", required = true)
            @PathParam("capaId") Long capaId) {
        service.excluirCapa(albumId, capaId);
        return Response.noContent().build();
    }
}
