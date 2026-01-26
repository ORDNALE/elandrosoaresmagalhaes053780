package com.elandroapi.modules.controllers;

import com.elandroapi.modules.services.CapaAlbumService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;

@Path("/v1/capas-album")
@Tag(name = "Capa de Álbum", description = "Gerenciamento de capas de álbuns")
@SecurityRequirement(name = "jwt")
public class CapaAlbumController {

    @Inject
    CapaAlbumService service;

    @GET
    @Path("/{id}/download")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Download de capa de álbum", description = "Gera uma URL pré-assinada para download da imagem da capa.")
    public Response download(
            @Parameter(description = "ID da capa do álbum", required = true) @PathParam("id") Long id) {
        try {
            String url = service.gerarUrlDownload(id);
            return Response.temporaryRedirect(new URI(url)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao gerar URL de download: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Excluir capa de álbum", description = "Remove a imagem da capa do MinIO e o registro do banco de dados.")
    public Response excluir(
            @Parameter(description = "ID da capa do álbum", required = true) @PathParam("id") Long id) {
        try {
            service.excluirCapa(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao excluir capa do álbum: " + e.getMessage())
                    .build();
        }
    }
}
