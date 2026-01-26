package com.elandroapi.modules.controllers;

import com.elandroapi.modules.dto.filter.AlbumFilter;
import com.elandroapi.modules.services.AlbumService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/v1/albuns")
@Tag(name = "Album", description = "Gerenciamento de álbuns")
@SecurityRequirement(name = "jwt")
@RolesAllowed({"USER", "ADMIN"})
public class AlbumController {

    @Inject
    AlbumService service;

    @GET
    @Operation(summary = "Listar álbuns", description = "Retorna uma lista paginada de álbuns com opções de filtro.")
    public Response listar(@BeanParam AlbumFilter filter) {
        return Response.ok(service.listar(filter)).build();
    }
}
