package com.elandroapi.modules.controllers;

import com.elandroapi.modules.dto.request.ArtistaRequest;
import com.elandroapi.modules.services.ArtistaService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import jakarta.ws.rs.DefaultValue;

@Path("/v1/artistas")
@Tag(name = "Artista", description = "Gerenciamento de artistas")
public class ArtistaController {

    @Inject
    ArtistaService service;

    @GET
    @Operation(summary = "Listar artistas", description = "Retorna uma lista de artistas com opção de filtro por nome e ordenação.")
    public Response listar(@QueryParam("nome") String nome, @QueryParam("sort") @DefaultValue("asc") String sort) {
        return Response.ok(service.listar(nome, sort)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar artista por ID", description = "Retorna os detalhes de um artista específico.")
    public Response buscar(@PathParam("id") Long id) {
        return Response.ok(service.buscar(id)).build();
    }

    @POST
    @Operation(summary = "Criar novo artista", description = "Cria um novo artista com os dados fornecidos.")
    public Response salvar(@Valid ArtistaRequest request) {
        return Response.ok(service.salvar(request)).status(Response.Status.CREATED).build();
    }

    @Path("/{id}")
    @PUT
    @Operation(summary = "Atualizar artista", description = "Atualiza os dados de um artista existente.")
    public Response atualizar(@PathParam("id") Long id, ArtistaRequest request) {
        service.atualizar(id, request);
        return Response.ok().build();
    }

    @Path("/{id}")
    @DELETE
    @Operation(summary = "Excluir artista", description = "Remove um artista do sistema.")
    public Response excluir(@PathParam("id") Long id) {
        service.excluir(id);
        return Response.ok().status(Response.Status.NO_CONTENT).build();
    }
}