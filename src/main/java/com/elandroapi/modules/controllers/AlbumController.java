package com.elandroapi.modules.controllers;

import com.elandroapi.modules.enums.TipoArtista;
import com.elandroapi.modules.services.AlbumService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/v1/albuns")
@Tag(name = "Album", description = "Gerenciamento de álbuns")
public class AlbumController {

    @Inject
    AlbumService service;

    @GET
    @Operation(summary = "Listar álbuns", description = "Retorna uma lista paginada de álbuns.")
    public Response listar(@QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("tipo") List<TipoArtista> tipos) {
        return Response.ok(service.listar(page, size, tipos)).build();
    }


}