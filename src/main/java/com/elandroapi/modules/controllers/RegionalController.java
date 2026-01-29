package com.elandroapi.modules.controllers;

import com.elandroapi.core.pagination.PageRequest;
import com.elandroapi.core.pagination.Paged;
import com.elandroapi.modules.dto.filter.RegionalFilterRequest;
import com.elandroapi.modules.dto.response.RegionalResponse;
import com.elandroapi.modules.dto.response.RegionalSyncResumoResponse;
import com.elandroapi.modules.services.RegionalService;
import com.elandroapi.modules.services.RegionalSyncService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/v1/regionais")
@Tag(name = "5. Regional", description = "Gerenciamento e sincronização de regionais")
@SecurityRequirement(name = "jwt")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RegionalController {

    @Inject
    RegionalService service;

    @Inject
    RegionalSyncService syncService;

    @GET
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(
            summary = "Listar regionais",
            description = "Retorna lista paginada de regionais com filtros opcionais por nome e status ativo."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Lista de regionais",
                    content = @Content(schema = @Schema(implementation = Paged.class))
            ),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado")
    })
    public Response listar(
            @BeanParam PageRequest pageRequest,
            @BeanParam RegionalFilterRequest filter) {
        return Response.ok(service.listar(pageRequest, filter)).build();
    }

    @POST
    @Path("/sync")
    @RolesAllowed({"ADMIN"})
    @Operation(
            summary = "Sincronizar regionais",
            description = "Sincroniza a tabela de regionais com a API externa."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Sincronização realizada com sucesso",
                    content = @Content(schema = @Schema(implementation = RegionalSyncResumoResponse.class))
            ),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado"),
            @APIResponse(responseCode = "403", description = "Acesso negado - requer permissão ADMIN")
    })
    public Response sincronizar() {
        return Response.ok(syncService.sincronizar()).build();
    }
}
