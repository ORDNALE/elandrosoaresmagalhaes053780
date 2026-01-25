package com.elandroapi.modules.controllers;


import com.elandroapi.modules.dto.response.CapaAlbumResponse;
import com.elandroapi.modules.services.CapaAlbumService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/v1/albuns")
@Tag(name = "Album", description = "Gerenciamento de álbuns")
@RolesAllowed({"USER", "ADMIN"})
public class CapaAlbumController {

    @Inject
    CapaAlbumService service;

    @POST
    @Path("/{id}/capas")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Upload de capa de álbum", description = "Realiza o upload de uma ou mais imagens para a capa de um álbum.")
    public Response upload(@PathParam("id") Long id, @RestForm("images") List<FileUpload> files) {
        if (files == null || files.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nenhuma imagem enviada")
                    .build();
        }

        List<CapaAlbumResponse> result = service.uploadCapas(id, files);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }
}