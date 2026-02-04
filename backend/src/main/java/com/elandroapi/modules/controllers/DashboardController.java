package com.elandroapi.modules.controllers;

import com.elandroapi.modules.dto.response.DashboardResponse;
import com.elandroapi.modules.services.DashboardService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/v1/dashboard")
@Tag(name = "Dashboard", description = "Informações consolidadas do sistema")
@SecurityRequirement(name = "jwt")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardController {

    @Inject
    DashboardService service;

    @GET
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(
            summary = "Obter totais do dashboard",
            description = "Retorna o total de artistas, álbuns e novidades cadastradas na semana corrente."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Dados retornados com sucesso",
                    content = @Content(schema = @Schema(implementation = DashboardResponse.class))
            ),
            @APIResponse(responseCode = "401", description = "Token JWT inválido ou expirado")
    })
    public Response obterTotais() {
        return Response.ok(service.obterTotais()).build();
    }
}
